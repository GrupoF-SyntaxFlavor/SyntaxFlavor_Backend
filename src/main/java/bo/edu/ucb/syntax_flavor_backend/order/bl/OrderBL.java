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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;
import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;

@Component
public class OrderBL {

    Logger LOGGER = LoggerFactory.getLogger(OrderBL.class);

    public static final String STATUS_PENDING = "Pendiente";
    public static final String STATUS_DELIVERED = "Entregado";
    public static final String STATUS_CANCELLED = "Cancelado";
    // TODO: Should add a status for "PAID"????

    private static final int MAX_ORDERS_PER_PAGE = 10;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemBL orderItemBL;

    @Autowired
    private CustomerBL customerBL;

    public Page<OrderDTO> listOrdersByFilters(String status, LocalDateTime minDate, LocalDateTime maxDate,
            Integer pageNumber, Integer pageSize, boolean sortAscending) {
        LOGGER.info("Listing orders by filters");
        Sort sort = sortAscending ? Sort.by("orderTimestamp").ascending() : Sort.by("orderTimestamp").descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Order> orderPage = null;
        if (status != null) {
            orderPage = orderRepository.findByMinDateBetweenMaxDateAndStatusByNameAsc(
                status, 
                minDate, 
                maxDate, 
                pageable);
        } else {
            orderPage = orderRepository.findByMinDateBetweenMaxDate(
                minDate, 
                maxDate, 
                pageable);
        }
        if (orderPage == null) {
            LOGGER.error("Error listing orders by datetime and status");
            throw new RuntimeException("Error listing orders by datetime and status");
        }
        return orderPage.map(order -> OrderDTO.fromEntity(order));
    }

    public Page<OrderDTO> listOrdersByStatus(int pageNumber, String status) {
        LOGGER.info("Listing orders by status");
        Pageable pageable = PageRequest.of(pageNumber, MAX_ORDERS_PER_PAGE);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        LOGGER.info("Querying with startOfDay: {}, endOfDay: {}, status: {}", startOfDay, endOfDay, status);
        Page<Order> orderPage = orderRepository.findAllByOrderTimestampBetweenAndStatusOrderByOrderTimestampAsc(
                startOfDay, endOfDay, status, pageable);
        if (orderPage == null) {
            LOGGER.error("Error listing orders by status");
            throw new RuntimeException("Error listing orders by status");
        }
        return orderPage.map(order -> OrderDTO.fromEntity(order));
    }

    public CartDTO createOrderFromCart(CartDTO cart) {
        LOGGER.info("Creating order from cart: {}", cart);

        CartDTO cartResponse = new CartDTO();
        Order order = new Order();
        try {
            order.setCustomerId(customerBL.findCustomerById(cart.getCustomerId()));
            order.setTableCode(cart.getTableCode());// adding table code to order
            order.setStatus(STATUS_PENDING);
            order.setOrderTimestamp(new Date());

            // Save the order and get the insertion ID
            order = orderRepository.save(order);
            orderItemBL.createOrderItemFromCartItemIds(cart.getItemIdQuantityMap(), order);

            // Set the order ID in the response
            cartResponse.setOrderId(order.getId());
            cartResponse.setCustomerId(order.getCustomerId().getId());
            cartResponse.setItemIdQuantityMap(cart.getItemIdQuantityMap());
            cartResponse.setTableCode(order.getTableCode());// adding table code to response

        } catch (Exception e) {
            LOGGER.error("Error creating order from cart: {}", e);
            throw e;
        }

        return cartResponse;
    }

    public OrderDTO setOrderStatusById(Integer orderId, String status) {
        LOGGER.info("Setting order status by ID: {}", orderId);
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            LOGGER.error("Order not found");
            throw new RuntimeException("Order not found");
        }
        if (status.equals(STATUS_CANCELLED) && order.getStatus().equals(STATUS_DELIVERED)) {
            LOGGER.error("Attempted to cancel an order that was already delivered");
            throw new IllegalStateException("Cannot cancel an order that has been delivered");
        }
        switch (status) {
            case STATUS_PENDING:
                order.setStatus(STATUS_PENDING);
                break;
            case STATUS_DELIVERED:
                order.setStatus(STATUS_DELIVERED);
                break;
            case STATUS_CANCELLED:
                order.setStatus(STATUS_CANCELLED);
                break;
            default:
                LOGGER.error("Invalid status");
                throw new RuntimeException("Invalid status");
        }

        order = orderRepository.save(order);
        return OrderDTO.fromEntity(order);
    }

    public Page<OrderDTO> listOrdersByCustomerId(int userId, String status, Integer pageNumber, Integer pageSize,
            boolean sortAscending) {

        Integer customerId = customerBL.findCustomerByUserId(userId).getId();

        LOGGER.info("Listing orders by customer ID: {}", customerId);
        Sort sort = sortAscending ? Sort.by("orderTimestamp").ascending() : Sort.by("orderTimestamp").descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Order> orderPage = null;

        if (status != null) {
            orderPage = orderRepository.findAllByCustomerIdAndStatusOrderByOrderTimestamp(customerId, status, pageable);
        } else {
            orderPage = orderRepository.findAllByCustomerIdOrderByOrderTimestamp(customerId, pageable);
        }

        if (orderPage == null) {
            LOGGER.error("Error listing orders by customer ID and status and order by timestamp");
            throw new RuntimeException("Error listing orders by customer ID and status and order by timestamp");
        }

        return orderPage.map(order -> OrderDTO.fromEntity(order));
    }
}
