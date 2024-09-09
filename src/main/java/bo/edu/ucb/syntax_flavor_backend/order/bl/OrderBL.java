package bo.edu.ucb.syntax_flavor_backend.order.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public class OrderBL {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderBL.class);

    public boolean createOrderFromCart(CartDTO cart) {
        LOGGER.info("Creating order from cart: {}", cart);
        
        Order o = new Order();
        

        return true;
    }
}
