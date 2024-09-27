package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
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
import org.springframework.web.multipart.MultipartFile;


@Component
public class BillBL {
    Logger LOGGER = LoggerFactory.getLogger(BillBL.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MinioFileService minioFileService;

    public BillBL(BillRepository billRepository, OrderRepository orderRepository) {
        this.billRepository = billRepository;
        this.orderRepository = orderRepository;
    }
    
    public Bill createBillFromOrder(BillRequestDTO billRequest) throws RuntimeException {
        LOGGER.info("Creating bill from order: {}", billRequest);
        try{
            Order order = orderRepository.findById(billRequest.getOrderId()).get();
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

    public String generateBillPdf(Bill bill) {
        LOGGER.info("Generating and uploading bill PDF for bill id: {}", bill.getId());
        try {
            // Create a PDF document
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            // Add content to the PDF using bill information
            document.add(new Paragraph("Bill ID: " + bill.getId()));
            document.add(new Paragraph("Customer: " + bill.getBillName()));
            document.add(new Paragraph("Total Amount: " + bill.getTotalCost()));
            // Add more bill details as needed

            document.close();

            // Now upload the PDF to Minio
            String fileName = "bills/pdf/" + bill.getId() + "/" + System.currentTimeMillis() + "_bill.pdf";
            String pdfUrl = minioFileService.uploadPdf(fileName, outputStream.toByteArray());

            LOGGER.info("Bill PDF uploaded successfully for bill id: {}", bill.getId());
            return pdfUrl;  // Return the URL of the uploaded PDF
        } catch (Exception e) {
            LOGGER.error("Error generating and uploading bill PDF: {}", e.getMessage());
            throw new RuntimeException("Error generating and uploading bill PDF: " + e.getMessage(), e);
        }
    }




}