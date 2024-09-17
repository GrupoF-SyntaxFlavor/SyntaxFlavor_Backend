package bo.edu.ucb.syntax_flavor_backend.menu.dto;

import java.math.BigDecimal;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;

public class MenuItemResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;

    // Constructor vacío
    public MenuItemResponseDTO() {
    }

    // Constructor con parámetros
    public MenuItemResponseDTO(Integer id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Constructor que recibe una entidad MenuItem
    public MenuItemResponseDTO(MenuItem menuItem) {
        this.id = menuItem.getId();
        this.name = menuItem.getName();
        this.description = menuItem.getDescription();
        this.price = menuItem.getPrice();
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
