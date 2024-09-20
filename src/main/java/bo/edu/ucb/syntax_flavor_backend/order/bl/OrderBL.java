package bo.edu.ucb.syntax_flavor_backend.order.bl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;
import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;

@Component
public class OrderBL {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderBL.class);

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_DELIVERED = "DELIVERED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    // TODO: Should add a status for "PAID"????

    private static final int MAX_ORDERS_PER_PAGE = 10;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemBL orderItemBL;

    @Autowired
    private CustomerBL customerBL;

    public Page<OrderDTO> listOrdersByDatetime(int pageNumber) {
        LOGGER.info("Listing orders by datetime");
        Pageable pageable = PageRequest.of(pageNumber, MAX_ORDERS_PER_PAGE);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        Page<Order> orderPage = orderRepository.findOrdersFromToday(startOfDay, endOfDay, pageable);
        if (orderPage == null) {
            LOGGER.error("Error listing orders by datetime");
            throw new RuntimeException("Error listing orders by datetime");
        }
        return orderPage.map(order -> OrderDTO.fromEntity(order));
    }


    public CartDTO createOrderFromCart(CartDTO cart) {
        LOGGER.info("Creating order from cart: {}", cart);
        
        CartDTO cartResponse = new CartDTO();
        Order order = new Order();
        try {
            order.setCustom(customerBL.findCustomerById(cart.getCustomerId()));
            order.setStatus(STATUS_PENDING);
            order.setOrderTimestamp(new Date()); 

            // Save the order and get the insertion ID
            order = orderRepository.save(order);
            orderItemBL.createOrderItemFromCartItemIds(cart.getItemIdQuantityMap(), order);

            // Set the order ID in the response
            cartResponse.setOrderId(order.getId());
            cartResponse.setCustomerId(order.getCustom().getId());
            cartResponse.setItemIdQuantityMap(cart.getItemIdQuantityMap());

        } catch (Exception e) {
            LOGGER.error("Error creating order from cart: {}", e);
            throw e;
        }

        return cartResponse;
    }
}
