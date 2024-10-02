package bo.edu.ucb.syntax_flavor_backend.bill.dto;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillRequestDTO {
    private Integer orderId;
    private String billName;
    private String nit;
    private BigDecimal totalCost;
    private String customerName;
    private String customerEmail;
}

