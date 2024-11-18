package bo.edu.ucb.syntax_flavor_backend.report.bl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderRepository;
import bo.edu.ucb.syntax_flavor_backend.report.dto.OrderReportKPIResponseDTO;


@Component
public class OrderReportBL {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderReportBL.class);

    @Autowired
    private OrderRepository orderRepository;

    public OrderReportKPIResponseDTO calculateOrderKPI(LocalDateTime startDate, LocalDateTime endDate) {
        LOGGER.info("Calculating Order KPI");
        OrderReportKPIResponseDTO orderReportKPIResponseDTO = new OrderReportKPIResponseDTO();
        List<Order> orders = orderRepository.findByMinDateBetweenMaxDateList(startDate, endDate);
        
        return orderReportKPIResponseDTO;
    }
}
