package bo.edu.ucb.syntax_flavor_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private Integer customerId;
    private String billName;
    private String nit;
}
