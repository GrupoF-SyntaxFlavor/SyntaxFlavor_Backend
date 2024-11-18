package bo.edu.ucb.syntax_flavor_backend.report.dto;

import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportKPIResponseDTO {
    private Integer totalOrders;
    private Integer totalAcceptedOrders;
    private Integer totalCancelledOrders;
    private String startDate;
    private String endDate;
    private List<OrderDTO> orders;

}
