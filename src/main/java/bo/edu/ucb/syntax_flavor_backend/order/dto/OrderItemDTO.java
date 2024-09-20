package bo.edu.ucb.syntax_flavor_backend.order.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Integer menuItemId;
    private String menuItemName;
    private BigDecimal price;
    private Integer quantity;

    public static OrderItemDTO fromMenuItemEntity(MenuItem menuItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setMenuItemId(menuItem.getId());
        orderItemDTO.setMenuItemName(menuItem.getName());
        orderItemDTO.setPrice(menuItem.getPrice());
        orderItemDTO.setQuantity(1);
        return orderItemDTO;
    }

    public static List<OrderItemDTO> fromMenuItemEntityList(Collection<MenuItem> menuItems) {
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for (MenuItem menuItem : menuItems) {
            orderItemDTOs.add(fromMenuItemEntity(menuItem));
        }
        return orderItemDTOs;
    }

    public static List<OrderItemDTO> fromEntityList(Collection<OrderItem> orderItems) {
        List<OrderItemDTO> orderItemsDTOs = new ArrayList<>();
        for (OrderItem orderItem : orderItems) {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            orderItemDTO.setMenuItemId(orderItem.getMenuItemId().getId());
            orderItemDTO.setMenuItemName(orderItem.getMenuItemId().getName());
            orderItemDTO.setPrice(orderItem.getMenuItemId().getPrice());
            orderItemDTO.setQuantity(orderItem.getQuantity());
            orderItemsDTOs.add(orderItemDTO);
        }
        return orderItemsDTOs;
    }
}
