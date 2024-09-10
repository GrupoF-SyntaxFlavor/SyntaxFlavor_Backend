package bo.edu.ucb.syntax_flavor_backend.order.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.order.bl.OrderBL;
import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;

@RestController
@RequestMapping(value = "/api/v1/order")
public class OrderAPI {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderAPI.class);

    @Autowired
    private OrderBL orderBL;

    @PostMapping
    public ResponseEntity<SyntaxFlavorResponse<CartDTO>> createOrderFromCart(CartDTO cart) {
        LOGGER.info("Endpoint POST /api/v1/order with cart: {}", cart);
        SyntaxFlavorResponse<CartDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            orderBL.createOrderFromCart(cart);
            sfr.setResponseCode("ORD-001");
            sfr.setPayload(cart);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }
}
