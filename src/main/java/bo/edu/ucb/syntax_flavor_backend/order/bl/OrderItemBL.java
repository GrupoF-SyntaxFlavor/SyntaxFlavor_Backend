package bo.edu.ucb.syntax_flavor_backend.order.bl;

import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.menu.bl.MenuBL;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;
import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import bo.edu.ucb.syntax_flavor_backend.order.repository.OrderItemRepository;

@Component
public class OrderItemBL {

    Logger LOGGER = LoggerFactory.getLogger(OrderItemBL.class);
    
    @Autowired 
    private MenuBL menuBL;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public boolean createOrderItemFromCartItemIds(Map<Integer, Integer> cartItemIdsQuantity, Order order) {
        LOGGER.info("Creating order item from cart item ids: {}", cartItemIdsQuantity);
        List<MenuItem> menuItems = menuBL.getMenuItemsFromIds(cartItemIdsQuantity.keySet());

        for(MenuItem item : menuItems) {
            LOGGER.info("Creating order item from menu item: {}", item);
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItemId(item);
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(cartItemIdsQuantity.get(item.getId()));
            orderItem.setOrderId(order);
            orderItemRepository.save(orderItem);
        }

        return true;
    }
}
