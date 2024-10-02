package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;
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
import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillRepository;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Optional;


@Component
public class BillBL {
    Logger LOGGER = LoggerFactory.getLogger(BillBL.class);

    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final MinioFileService minioFileService;

    @Autowired
    public BillBL(BillRepository billRepository, OrderRepository orderRepository, MinioFileService minioFileService) {
        this.billRepository = billRepository;
        this.orderRepository = orderRepository;
        this.minioFileService = minioFileService;
    }

    public Bill createBillFromOrder(BillRequestDTO billRequest) throws RuntimeException {
        LOGGER.info("Creating bill from order: {}", billRequest);
        try{
            Optional<Order> optionalOrder = orderRepository.findById(billRequest.getOrderId());
            if (optionalOrder.isEmpty()) {
                throw new RuntimeException("Order with id " + billRequest.getOrderId() + " not found");
            }
            Order order = optionalOrder.get();
            Bill createdBill = new Bill();
            createdBill.setOrdersId(order);
            createdBill.setBillName(billRequest.getBillName());
            createdBill.setNit(billRequest.getNit());
            createdBill.setTotalCost(billRequest.getTotalCost());

            billRepository.save(createdBill);
            return createdBill;
        }
        catch(Exception e) {
            LOGGER.error("Error creating bill from order: {}", e.getMessage());
            throw new RuntimeException("Error creating bill from order: " + e.getMessage());
        }
    }

    public byte[] generateBillPdfBytes(Bill bill) {
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
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : bill.getOrdersId().getOrderItemsCollection()) {
                totalAmount = totalAmount.add(item.getPrice().multiply(new BigDecimal(item.getQuantity())));
            }

            // Add total amount to the PDF
            Paragraph totalAmountParagraph = new Paragraph("Total Amount: " + totalAmount);
            totalAmountParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalAmountParagraph);

            document.close();

            // Return the PDF as a byte array
            return outputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Error generating bill PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating bill PDF: " + e.getMessage(), e);
        }
    }


    public String generateBillPdf(Bill bill) {
        LOGGER.info("Generating and uploading bill PDF for bill id: {}", bill.getId());
        try {
            // Generate PDF bytes
            byte[] pdfBytes = generateBillPdfBytes(bill);

            // Now upload the PDF to Minio
            String fileName = "bills/pdf/" + bill.getId() + "/" + System.currentTimeMillis() + "_bill.pdf";
            String pdfUrl = minioFileService.uploadFile(fileName, pdfBytes, "application/pdf");

            LOGGER.info("Bill PDF uploaded successfully for bill id: {}", bill.getId());
            return pdfUrl;  // Return the URL of the uploaded PDF
        } catch (Exception e) {
            LOGGER.error("Error generating and uploading bill PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating and uploading bill PDF: " + e.getMessage(), e);
        }
    }
}