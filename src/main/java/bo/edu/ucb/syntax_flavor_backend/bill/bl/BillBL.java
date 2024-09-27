package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import bo.edu.ucb.syntax_flavor_backend.service.MinioFileService;
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


    public byte[] generateBillPdf(Integer billId, Bill bill) {
        try (ByteArrayOutputStream pdfStream = new ByteArrayOutputStream()) {
            // Implementación de la lógica de generación de PDF

            // Create a MultipartFile from the ByteArrayOutputStream
            MultipartFile multipartFile = new ByteArrayMultipartFile(
                    pdfStream.toByteArray(),
                    "bill.pdf", // filename
                    "application/pdf" // content type
            );

            // Now you can upload the MultipartFile to Minio
            String pdfUrl = minioFileService.uploadFile(multipartFile);

            return pdfStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para validar si el tipo de contenido es un PDF
    private boolean isPdf(String contentType) {
        return contentType.equals("application/pdf");
    }


}