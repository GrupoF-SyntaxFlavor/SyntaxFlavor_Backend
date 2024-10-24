package bo.edu.ucb.syntax_flavor_backend.menu.dto;

import java.math.BigDecimal;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private Integer quantity = 10000; //TODO: Currently hardcoded, should be dynamic
    private Boolean status;

    // Constructor that receives a MenuItem entity
    public MenuItemResponseDTO(MenuItem menuItem) {
        this.id = menuItem.getId();
        this.name = menuItem.getName();
        this.description = menuItem.getDescription();
        this.image = menuItem.getImageUrl();
        this.price = menuItem.getPrice();
        this.status = menuItem.getStatus();
    }
}
