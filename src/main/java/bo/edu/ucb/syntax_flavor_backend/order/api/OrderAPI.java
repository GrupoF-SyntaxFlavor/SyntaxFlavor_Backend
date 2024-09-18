package bo.edu.ucb.syntax_flavor_backend.order.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping(value = "/api/v1/order")
public class OrderAPI {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderAPI.class);

    @Autowired
    private OrderBL orderBL;

    @Operation(summary = "List orders by datetime", description = "Can page through orders by datetime, the displayed orders are the most recent ones. No filters are applied at this moment.")
    @GetMapping
    public ResponseEntity<SyntaxFlavorResponse<Page<OrderDTO>>> listOrdersByDatetime(@RequestParam int pageNumber) {
        LOGGER.info("Endpoint GET /api/v1/order with pageNumber: {}", pageNumber);
        SyntaxFlavorResponse<Page<OrderDTO>> sfr = new SyntaxFlavorResponse<>();
        try {
            Page<OrderDTO> orders = orderBL.listOrdersByDatetime(pageNumber);
            sfr.setResponseCode("ORD-000");
            sfr.setPayload(orders);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @Operation(summary = "Create order from cart", description = "Creates an order from a cart. The cart must have a customer ID and a map of menu item IDs to quantities ordered. Returns the same object plus an ID of insertion")
    @PostMapping
    public ResponseEntity<SyntaxFlavorResponse<CartDTO>> createOrderFromCart(@RequestBody CartDTO cart) {
        LOGGER.info("Endpoint POST /api/v1/order with cart: {}", cart);
        SyntaxFlavorResponse<CartDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            CartDTO cartResponse = orderBL.createOrderFromCart(cart);
            sfr.setResponseCode("ORD-001");
            sfr.setPayload(cartResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }
}
