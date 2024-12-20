package bo.edu.ucb.syntax_flavor_backend.report.bl;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderItemRepository;
import bo.edu.ucb.syntax_flavor_backend.report.dto.MostSoldMenuItemDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class MenuItemReportBL {
    Logger LOGGER = LoggerFactory.getLogger(MenuItemReportBL.class);

    @Autowired
    private OrderItemRepository orderItemRepository;

    

    public List<MostSoldMenuItemDTO> getMostSoldMenuItems(LocalDateTime startDate, LocalDateTime endDate, Integer top) {

        // Obtener datos en bruto
        // List<Object[]> rawData = orderItemRepository.findMostSoldMenuItems(startDate, endDate);
        Pageable topX = PageRequest.of(0, top); // Cambia X por el número de resultados que quieres.
        List<Object[]> topMenuItems = orderItemRepository.findMostSoldMenuItems(startDate, endDate, topX);


        // Calcular totales y poblar DTOs
        BigDecimal totalRevenue = topMenuItems.stream()
                                         .map(row -> (BigDecimal) row[2]) // totalPrice
                                            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MostSoldMenuItemDTO> items = new ArrayList<>();
        for (Object[] row : topMenuItems) {
            Integer menuItemId = (Integer) row[0];
            String menuItemName = (String) row[1];
            BigDecimal totalPrice = (BigDecimal) row[2];
            Long totalQuantity = (Long) row[3];
            // Double percentage = totalPrice.divide(totalRevenue, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
            Double percentage = totalQuantity.doubleValue() / totalRevenue.doubleValue() * 100;
            
            items.add(new MostSoldMenuItemDTO(menuItemId, menuItemName, totalPrice, totalQuantity, percentage));
        }

        return items;
    }
}