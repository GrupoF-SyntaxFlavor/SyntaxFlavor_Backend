package bo.edu.ucb.syntax_flavor_backend.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
}
