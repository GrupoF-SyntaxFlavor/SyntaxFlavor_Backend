package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import bo.edu.ucb.syntax_flavor_backend.service.EmailService;
import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;
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
        LOGGER.info("Creating bill from order: {}", billRequest);
        Bill createdBill = new Bill();
        BillPdf billPdf = new BillPdf();
        try {
            // Attempt to retireve the order
            Order order = orderRepository.findById(billRequest.getOrderId()).orElseThrow(() -> new RuntimeException("Order not found"));

            // Attempt to create the bill
            createdBill.setOrdersId(order);
            // if both billing_name and NIT are present, then create the bill with those values
            // alternatively, if a userId is present, then use the billing name and NIT from the user
            // if none of the above are present, then throw an exception
            if (billRequest.getBillName() != null && billRequest.getNit() != null) {
                createdBill.setBillName(billRequest.getBillName());
                createdBill.setNit(billRequest.getNit());
            } else if (billRequest.getUserId() != null) {
                createdBill.setBillName(order.getCustomerId().getBillName());
                createdBill.setNit(order.getCustomerId().getNit());
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
            String fileName = "bills/pdf/" + createdBill.getId() + "/" + System.currentTimeMillis() + "_" + "bill."+ createdBill.getId() + ".pdf";
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
        String emailBody = "Dear customer, please find attached the bill for your order: ORD-" + orderNumber;
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
                    "bill" + orderNumber + ".pdf");
        } catch (Exception e) {
            LOGGER.error("Error sending bill email: {}", e.getMessage());
            throw new BillGenerationException("Error sending bill email: " + e.getMessage(), 4);
        }
    }

    private byte[] generateBillPdf(Bill bill) throws BillGenerationException {
        LOGGER.info("Generating PDF for bill id: {}", bill.getId());
        try {
            // Create a PDF document
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add a custom title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Bill Details", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Add a line
            PdfContentByte cb = writer.getDirectContent();
            cb.setLineWidth(1f);
            cb.moveTo(20, writer.getVerticalPosition(false) - 10);
            cb.lineTo(document.right() - 20, writer.getVerticalPosition(false) - 10);
            cb.stroke();

            // Add content to the PDF using bill information
            PdfPTable table = new PdfPTable(2); // 2 columns
            PdfPCell cell1 = new PdfPCell(new Phrase("Bill ID:"));
            PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(bill.getId())));
            table.addCell(cell1);
            table.addCell(cell2);
            document.add(table);

            // Calculate total amount
            BigDecimal totalAmount = getTotalCostByOrderId(bill.getOrdersId().getId());

            // Add total amount to the PDF
            Paragraph totalAmountParagraph = new Paragraph("Total Amount: " + totalAmount);
            totalAmountParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalAmountParagraph);

            document.close();

            // Return the PDF as a byte array
            return outputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Error generating bill PDF: {}", e.getMessage());
            throw new BillGenerationException("Error generating bill PDF: " + e.getMessage(), 1);
        }
    }
/* 
    public String generateBillPdf(Bill bill) {
        LOGGER.info("Generating and uploading bill PDF for bill id: {}", bill.getId());
        try {
            // Generate PDF bytes
            byte[] pdfBytes = generateBillPdfBytes(bill);

            // Now upload the PDF to Minio
            String fileName = "bills/pdf/" + bill.getId() + "/" + System.currentTimeMillis() + "_bill.pdf";
            String pdfUrl = minioFileService.uploadFile(fileName, pdfBytes, "application/pdf");

            LOGGER.info("Bill PDF uploaded successfully for bill id: {}", bill.getId());
            return pdfUrl; // Return the URL of the uploaded PDF
        } catch (Exception e) {
            LOGGER.error("Error generating and uploading bill PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating and uploading bill PDF: " + e.getMessage(), e);
        }
    } */

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