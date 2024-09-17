package bo.edu.ucb.syntax_flavor_backend.bill.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.bill.dto.BillRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.bill.repository.BillRepository;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;


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
}
