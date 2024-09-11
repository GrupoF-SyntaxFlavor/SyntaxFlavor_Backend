package bo.edu.ucb.syntax_flavor_backend.order.dto;

import java.util.ArrayList;
import java.util.List;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Integer orderId;
    private String customerName;
    private String cutomerTable;
    private String orderStatus;
    private String orderTimestamp;
    private List<OrderItemDTO> orderItems;

    public static OrderDTO fromEntity(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getId());
        orderDTO.setCustomerName(order.getCustom().getUsersId().getName());
        orderDTO.setCutomerTable("0"); // TODO: this will be set later from the request
        orderDTO.setOrderStatus(order.getStatus());
        orderDTO.setOrderTimestamp(order.getOrderTimestamp().toString());
        orderDTO.setOrderItems(OrderItemDTO.fromEntityList(order.getOrderItemsCollection()));
        return orderDTO;
    }

    public static List<OrderDTO> fromEntityList(List<Order> orders) {
        List<OrderDTO> orderDTOs = new ArrayList<>();
        for (Order order : orders) {
            OrderDTO orderDTO = fromEntity(order);
            orderDTOs.add(orderDTO);
        }
        return orderDTOs;
    }
}
