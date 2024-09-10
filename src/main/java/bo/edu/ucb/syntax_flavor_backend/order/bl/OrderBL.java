package bo.edu.ucb.syntax_flavor_backend.order.bl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import bo.edu.ucb.syntax_flavor_backend.menu.bl.MenuBL;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;
import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;

public class OrderBL {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderBL.class);

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemBL orderItemBL;

    @Autowired
    private CustomerBL customerBL;


    public boolean createOrderFromCart(CartDTO cart) {
        LOGGER.info("Creating order from cart: {}", cart);
        
        Order order = new Order();
        try {
            order.setCustom(customerBL.findCustomerById(cart.getCustomerId()));
            order.setStatus(STATUS_PENDING);
            order.setOrderTimestamp(null); // TODO: set the current timestamp. Will need a change to DB
        
            orderRepository.save(order);
            orderItemBL.createOrderItemFromCartItemIds(cart.getItemIdQuantityMap(), order); // TODO: test this
        } catch (Exception e) {
            LOGGER.error("Error creating order from cart: {}", e);
            throw e;
        }

        return true;
    }
}
