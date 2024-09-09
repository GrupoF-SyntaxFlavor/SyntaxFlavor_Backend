package bo.edu.ucb.syntax_flavor_backend.order.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer customerId;
    private List<Integer> productIdList;
}
