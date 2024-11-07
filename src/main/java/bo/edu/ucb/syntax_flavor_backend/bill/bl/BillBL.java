package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import bo.edu.ucb.syntax_flavor_backend.service.EmailService;
import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;
import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;
import bo.edu.ucb.syntax_flavor_backend.util.BillGenerationException;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillResponseDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.bill.entity.BillPdf;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillPdfRepository;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillRepository;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;

import java.math.BigDecimal;

@Component
public class BillBL {
    Logger LOGGER = LoggerFactory.getLogger(BillBL.class);

    @Autowired
    private CustomerBL customerBL;

    @Autowired
    private BillPdfRepository billPdfRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MinioFileService minioFileService;

    @Autowired
    private EmailService emailService;

    public BillResponseDTO createBillFromOrder(BillRequestDTO billRequest) throws RuntimeException, BillGenerationException {
        // FIXME: At this point this workds but using the customerBL is not the best approach, maybe a saga pattern would be better
        LOGGER.info("Creating bill from order: {}", billRequest);
        Bill createdBill = new Bill();
        BillPdf billPdf = new BillPdf();
        try {
            // Attempt to retireve the order
            Order order = orderRepository.findById(billRequest.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));
            LOGGER.info("Order found: {}", order.getId());
            // Attempt to create the bill
            createdBill.setOrdersId(order);
            // if both billing_name and NIT are present, then create the bill with those values
            // alternatively, if a userId is present, then use the billing name and NIT from the user
            // if none of the above are present, then throw an exception
            if (billRequest.getBillName() != null && billRequest.getNit() != null) {
                createdBill.setBillName(billRequest.getBillName());
                createdBill.setNit(billRequest.getNit());
            } else if (billRequest.getUserId() != null) {
                // This is assuming that the person who placed the order is the same as the person who will receive the bill
                // Maybe there's a better way to check this
                createdBill.setBillName(customerBL.getBillingInfo(billRequest.getUserId()).getBillName());
                createdBill.setNit(customerBL.getBillingInfo(billRequest.getUserId()).getNit());
            } else {
                throw new RuntimeException("Billing name and NIT are required");
            }


            createdBill.setTotalCost(getTotalCostByOrderId(order.getId()));

            billRepository.save(createdBill);
            LOGGER.info("Succesfully created bill for order: {}", order.getId());

            //
            byte[] pdfBytes = generateBillPdf(createdBill);
            LOGGER.info("Succesfully generated pdf for bill, attempting to upload to minIO");
            // Generar un nombre de archivo único
            String fileName = "bills/pdf/" + createdBill.getId() + "/" + System.currentTimeMillis() + "_" + "bill-"+ createdBill.getId() + ".pdf";
            LOGGER.info("Uploading PDF to minio as {}", fileName);
            String fileUrl = minioFileService.uploadPdf(fileName, pdfBytes); 
            LOGGER.info("Succesfully uploaded to minio as {} saving to Database", fileUrl);
            billPdf.setBillId(createdBill);
            billPdf.setPdfUrl(fileUrl);
            String recipientEmail = order.getCustomerId().getUsersId().getEmail();
            LOGGER.info("Sending bill to email: {}", recipientEmail);
            billPdf.setSentTo(recipientEmail);
            billPdfRepository.save(billPdf);

            // Attempt to send the bill email
            BillResponseDTO billResponse = new BillResponseDTO(createdBill);
            sendBillEmail(billPdf);
            billPdf.setSentAt(new java.util.Date());
            billPdfRepository.save(billPdf);
            return billResponse;
        }  catch (BillGenerationException billException) {
            switch (billException.getGenerationProcessErrorCode()) {
                case 0:
                    LOGGER.error("Error saving bill from order", billException.getMessage());
                    throw new RuntimeException("Error saving bill from order " + billException.getMessage()); 
                case 1:
                    LOGGER.error("Error generating PDF for bill", billException.getMessage());
                    throw new RuntimeException("Error generating PDF for bill, Bill still created " + billException.getMessage());   
                case 2:
                    LOGGER.error("Error saving PDF to minio", billException.getMessage());
                    throw new RuntimeException("Error saving PDF to minio, Bill still created " + billException.getMessage());   
                case 3:
                    LOGGER.error("Error generating email {}", billException.getMessage());
                    throw new RuntimeException("Error generating bill PDF. Bill still created: " + billException.getMessage());
                case 4:
                    LOGGER.error("Error sending email {}", billException.getMessage());
                    throw new RuntimeException("Error sending mail to user. Bill still created and stored in minio: " + billException.getMessage());
                default:
                    LOGGER.error("Error during createBillFromOrder: {}", billException.getMessage());
                    throw new RuntimeException("Unidentified error during creation of bill from Order " + billException.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Error during createBillFromOrder: {}", e.getMessage());
            throw new RuntimeException("Unidentified error during creation of bill from Order " + e.getMessage());
        }
    }

    private void sendBillEmail(BillPdf billPdf) throws BillGenerationException {
        Integer orderNumber = billPdf.getBillId().getOrdersId().getId();
        if (billPdf.getPdfUrl().isBlank()) throw new BillGenerationException("PDF URL is blank", 3);
        if (billPdf.getSentTo().isBlank()) throw new BillGenerationException("Sent to email is blank", 3);
        // Prepare the email to send
        String emailSubject = "Bill for order ORD-" + orderNumber;
        String emailBody = "Dear customer, please find attached the bill for your order: ORD-" + orderNumber; // TODO: change to use a HTML in MIME
        // The object key is bills/pdf/ + the last two parts of the PDF URL
        String objectKey = "bills/pdf/" + billPdf.getBillId().getId() + "/" + billPdf.getPdfUrl().substring(billPdf.getPdfUrl().lastIndexOf("/"));
        byte[] attachment = minioFileService.getFile(objectKey);
        

        try {
            // Send the email with the attached PDF
            emailService.sendEmailWithAttachment(
                    billPdf.getSentTo(),
                    emailSubject,
                    emailBody,
                    attachment, // Use the PDF byte array as the attachment
                    "bill-" + orderNumber + ".pdf");
        } catch (Exception e) {
            LOGGER.error("Error sending bill email: {}", e.getMessage());
            throw new BillGenerationException("Error sending bill email: " + e.getMessage(), 4);
        }
    }

    private byte[] generateBillPdf(Bill bill) throws BillGenerationException {
        LOGGER.info("Generating PDF for bill id: {}", bill.getId());
        // FIXME: Maybe this should be a util
        try {
            // Create a PDF document
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add a custom title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph title = new Paragraph("Proforma de factura", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add a line
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(1f);
            cb.moveTo(20, writer.getVerticalPosition(false) - 10);
            cb.lineTo(document.right() - 10, writer.getVerticalPosition(false) - 10);
            cb.stroke();

            // add some space
            document.add(new Paragraph(" "));

            // Add content to the PDF using bill information
            PdfPTable table = new PdfPTable(2); // 2 columns
            table.setWidthPercentage(100); // Set table width to 100% of the page width
            float[] columnWidths1 = {3f, 1f}; // Set column widths (1/5, 4/5)
            table.setWidths(columnWidths1);
            PdfPCell cell1 = new PdfPCell(new Phrase("Código de Factura:", headerFont));
            PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(bill.getId())));
            table.addCell(cell1);
            table.addCell(cell2);
            document.add(table);

            // Add some space
            document.add(new Paragraph(" "));

            // Add the billing name and NIT
            PdfPTable billingTable = new PdfPTable(2); // 2 columns
            billingTable.setWidthPercentage(100); // Set table width to 100% of the page width
            float[] columnWidths2 = {1f, 1f}; // Set column widths (1/5, 4/5)
            billingTable.setWidths(columnWidths2); // Set column widths (2/5, 3/5)
            PdfPCell dateCell1 = new PdfPCell(new Phrase("Fecha de Facturación:"));
            PdfPCell dateCell2 = new PdfPCell(new Phrase(bill.getCreatedAt().toString()));
            PdfPCell billingCell1 = new PdfPCell(new Phrase("Nombre de Facturación:"));
            PdfPCell billingCell2 = new PdfPCell(new Phrase(bill.getBillName()));
            PdfPCell nitCell1 = new PdfPCell(new Phrase("NIT:"));
            PdfPCell nitCell2 = new PdfPCell(new Phrase(bill.getNit()));

            billingTable.addCell(dateCell1);
            billingTable.addCell(dateCell2);
            billingTable.addCell(billingCell1);
            billingTable.addCell(billingCell2);
            billingTable.addCell(nitCell1);
            billingTable.addCell(nitCell2);

            document.add(billingTable);

            // Add some space
            document.add(new Paragraph(" "));

            // For each element in the order, add a row to the PDF with its quantity and unit price
            Order order = bill.getOrdersId();
            PdfPTable orderTable = new PdfPTable(3); // 3 columns
            orderTable.setWidthPercentage(100); // Set table width to 100% of the page width
            float[] columnWidths3 = {3f, 1f, 1f}; // Set column widths (3/5, 1/5, 1/5)
            orderTable.setWidths(columnWidths3);
            
            PdfPCell orderCell1 = new PdfPCell(new Phrase("Producto", headerFont));
            PdfPCell orderCell2 = new PdfPCell(new Phrase("Cantidad", headerFont));
            PdfPCell orderCell3 = new PdfPCell(new Phrase("Precio", headerFont));
            orderTable.addCell(orderCell1);
            orderTable.addCell(orderCell2);
            orderTable.addCell(orderCell3);

            for (OrderItem item : order.getOrderItemsCollection()) {
                LOGGER.info("Adding item to PDF: {}", item.getMenuItemId().getName());
                PdfPCell itemCell1 = new PdfPCell(new Phrase(item.getMenuItemId().getName()));
                PdfPCell itemCell2 = new PdfPCell(new Phrase(String.valueOf(item.getQuantity())));
                PdfPCell itemCell3 = new PdfPCell(new Phrase(String.valueOf(item.getPrice())));
                
                // Align the quantity and price cells to the right
                itemCell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
                itemCell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
                
                orderTable.addCell(itemCell1);
                orderTable.addCell(itemCell2);
                orderTable.addCell(itemCell3);
            }

            document.add(orderTable);

            // Calculate total amount
            BigDecimal totalAmount = getTotalCostByOrderId(bill.getOrdersId().getId());

            // Add total amount to the PDF in a table aligned with the unitPrice column
            PdfPTable totalTable = new PdfPTable(3); // 3 columns to match the orderTable
            totalTable.setWidthPercentage(100); // Set table width to 100% of the page width
            float[] totalColumnWidths = {3f, 1f, 1f}; // Set column widths (3/5, 1/5, 1/5)
            totalTable.setWidths(totalColumnWidths);

            PdfPCell emptyCell1 = new PdfPCell(new Phrase("")); // Empty cell for the first column
            PdfPCell emptyCell2 = new PdfPCell(new Phrase("Monto Total: ", headerFont)); // Empty cell for the second column
            PdfPCell totalAmountCell = new PdfPCell(new Phrase(totalAmount.toString(), headerFont));
            totalAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT); // Align text to the right

            totalTable.addCell(emptyCell1);
            totalTable.addCell(emptyCell2);
            totalTable.addCell(totalAmountCell);

            document.add(totalTable);

            // Add some more space
            document.add(new Paragraph(" "));

            // Add a disclosure at the end
            Paragraph disclosureParagraph = new Paragraph(
                "La presente es una proforma de factura, no cuenta con valor fiscal.\n\n"+
                "Todos los derechos reservados de SyntaxFlavor"
            );
            // Align at the bottom center set with small font
            disclosureParagraph.setAlignment(Element.ALIGN_CENTER);
            disclosureParagraph.setFont(new Font(Font.FontFamily.COURIER, 4, Font.ITALIC));
            document.add(disclosureParagraph);

            document.close();

            // Return the PDF as a byte array
            return outputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Error generating bill PDF: {}", e.getMessage());
            throw new BillGenerationException("Error generating bill PDF: " + e.getMessage(), 1);
        }
    }

    private BigDecimal getTotalCostByOrderId(Integer orderId) {
        LOGGER.info("Getting total cost by order ID: {}", orderId);
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            LOGGER.error("Order not found");
            throw new RuntimeException("Order not found");
        }
        
        BigDecimal totalCost = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItemsCollection()) {
            totalCost = totalCost.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        return totalCost;
    }
}