package bo.edu.ucb.syntax_flavor_backend.order.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer orderId;
    private Integer customerId;
    private Map<Integer, Integer> itemIdQuantityMap;
}
