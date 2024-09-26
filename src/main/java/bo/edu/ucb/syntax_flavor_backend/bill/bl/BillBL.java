package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillRepository;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@Component
public class BillBL {
    Logger LOGGER = LoggerFactory.getLogger(BillBL.class);

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    public ByteArrayOutputStream generateBillPdf(Bill createdBill) {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        try {
            // Implement your PDF generation logic here
            // Use the createdBill object to get the data needed for the PDF
            // Example:
            // PdfWriter.getInstance(document, pdfStream);
            // document.open();
            // document.add(new Paragraph("Bill ID: " + createdBill.getId()));
            // document.add(new Paragraph("Customer NIT: " + createdBill.getNit()));
            // document.add(new Paragraph("Total Cost: " + createdBill.getTotalCost()));
            // document.close();

            // Return the generated PDF stream
            return pdfStream;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        } finally {
            try {
                pdfStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
