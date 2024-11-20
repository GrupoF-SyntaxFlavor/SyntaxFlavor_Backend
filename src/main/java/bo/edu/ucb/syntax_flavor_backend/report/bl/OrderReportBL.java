package bo.edu.ucb.syntax_flavor_backend.report.bl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;
import bo.edu.ucb.syntax_flavor_backend.report.dto.OrderReportKPIResponseDTO;


@Component
public class OrderReportBL {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderReportBL.class);

    public static final String STATUS_DELIVERED = "Entregado";
    public static final String STATUS_CANCELLED = "Cancelado";

    @Autowired
    private OrderRepository orderRepository;

    public OrderReportKPIResponseDTO calculateOrderKPI(LocalDateTime startDate, LocalDateTime endDate) {
        LOGGER.info("Calculating Order KPI");
        OrderReportKPIResponseDTO orderReportKPIResponseDTO = new OrderReportKPIResponseDTO();
        List<Order> orders;
        try {
            // Intentar obtener las órdenes desde el repositorio
            orders = orderRepository.findByMinDateBetweenMaxDateList(startDate, endDate);
        } catch (DataAccessException e) {
            // Manejar errores relacionados con la conexión o la base de datos
            LOGGER.error("Database connection failed while fetching orders: {}", e.getMessage());
            throw new RuntimeException("Failed to connect to the database. Please try again later.", e);
        }
        if(orders.isEmpty()){
            LOGGER.info("No orders found between {} and {}", startDate, endDate);
            orderReportKPIResponseDTO.setTotalOrders(0);
            orderReportKPIResponseDTO.setTotalAcceptedOrders(0);
            orderReportKPIResponseDTO.setTotalCancelledOrders(0);
            orderReportKPIResponseDTO.setStartDate(startDate.toString());
            orderReportKPIResponseDTO.setEndDate(endDate.toString());
            return orderReportKPIResponseDTO;
        }
        // Calcular el total de órdenes
        int totalOrders = orders.size();

        // Filtrar y contar las órdenes aceptadas y canceladas
        int totalAcceptedOrders = (int) orders.stream()
                .filter(order -> STATUS_DELIVERED.equalsIgnoreCase(order.getStatus()))
                .count();

        int totalCancelledOrders = (int) orders.stream()
                .filter(order -> STATUS_CANCELLED.equalsIgnoreCase(order.getStatus()))
                .count();

        // Convertir las órdenes a DTOs
        //List<OrderDTO> orderDTOs = OrderDTO.fromEntityList(orders);

        orderReportKPIResponseDTO.setTotalOrders(totalOrders);
        orderReportKPIResponseDTO.setTotalAcceptedOrders(totalAcceptedOrders);
        orderReportKPIResponseDTO.setTotalCancelledOrders(totalCancelledOrders);
        orderReportKPIResponseDTO.setStartDate(startDate.toString());
        orderReportKPIResponseDTO.setEndDate(endDate.toString());

        return orderReportKPIResponseDTO;
    }
}
