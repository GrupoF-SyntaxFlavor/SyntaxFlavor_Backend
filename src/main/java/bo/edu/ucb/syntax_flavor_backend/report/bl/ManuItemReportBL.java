package bo.edu.ucb.syntax_flavor_backend.report.bl;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderItemRepository;
import bo.edu.ucb.syntax_flavor_backend.report.dto.MostSoldMenuItemDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ManuItemReportBL {
    Logger LOGGER = LoggerFactory.getLogger(ManuItemReportBL.class);

    @Autowired
    private OrderItemRepository orderItemRepository;

    

    public List<MostSoldMenuItemDTO> getMostSoldMenuItems(String period) {
        Date startDate; // Calcular según el período
        Date endDate = new Date();

        switch (period.toLowerCase()) {
            case "semanal":
                // Hace una semana desde hoy
                startDate = Date.from(LocalDate.now()
                                            .minusWeeks(1)
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant());
                break;
            case "mensual":
                // Hace un mes desde hoy
                startDate = Date.from(LocalDate.now()
                                            .minusMonths(1)
                                            .atStartOfDay(ZoneId.systemDefault())
                                            .toInstant());
                break;
            default:
                throw new IllegalArgumentException("Período no válido. Usa 'semanal' o 'mensual'.");
        }

        // Obtener datos en bruto
        List<Object[]> rawData = orderItemRepository.findMostSoldMenuItems(startDate, endDate);

        // Calcular totales y poblar DTOs
        BigDecimal totalRevenue = rawData.stream()
                                         .map(row -> (BigDecimal) row[2]) // totalPrice
                                         .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MostSoldMenuItemDTO> items = new ArrayList<>();
        for (Object[] row : rawData) {
            Integer menuItemId = (Integer) row[0];
            String menuItemName = (String) row[1];
            BigDecimal totalPrice = (BigDecimal) row[2];
            Long totalQuantity = (Long) row[3];
            Double percentage = totalPrice.divide(totalRevenue, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();

            items.add(new MostSoldMenuItemDTO(menuItemId, menuItemName, totalPrice, totalQuantity, percentage));
        }

        return items;
    }
}