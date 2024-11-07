package bo.edu.ucb.syntax_flavor_backend.bill.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillRequestDTO {
    private Integer orderId;
    private Integer userId;
    private String billName;
    private String nit;
}

