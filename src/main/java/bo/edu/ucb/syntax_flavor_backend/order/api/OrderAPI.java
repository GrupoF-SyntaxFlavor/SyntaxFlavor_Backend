package bo.edu.ucb.syntax_flavor_backend.order.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.order.bl.OrderBL;
import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;

@RestController
@RequestMapping(value = "/api/v1/order")
public class OrderAPI {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderAPI.class);

    @Autowired
    private OrderBL orderBL;

    @GetMapping
    public ResponseEntity<SyntaxFlavorResponse<List<OrderDTO>>> listOrdersByDatetime(@RequestParam int pageNumber) {
        LOGGER.info("Endpoint GET /api/v1/order with pageNumber: {}", pageNumber);
        SyntaxFlavorResponse<List<OrderDTO>> sfr = new SyntaxFlavorResponse<>();
        try {
            List<OrderDTO> orders = orderBL.listOrdersByDatetime(pageNumber);
            sfr.setResponseCode("ORD-000");
            sfr.setPayload(orders);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @PostMapping
    public ResponseEntity<SyntaxFlavorResponse<CartDTO>> createOrderFromCart(@RequestBody CartDTO cart) {
        LOGGER.info("Endpoint POST /api/v1/order with cart: {}", cart);
        SyntaxFlavorResponse<CartDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            orderBL.createOrderFromCart(cart);
            sfr.setResponseCode("ORD-001");
            sfr.setPayload(cart);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }
}
