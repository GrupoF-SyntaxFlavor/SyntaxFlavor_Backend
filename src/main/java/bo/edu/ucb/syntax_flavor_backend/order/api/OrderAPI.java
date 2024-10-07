package bo.edu.ucb.syntax_flavor_backend.order.api;

import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;
import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bo.edu.ucb.syntax_flavor_backend.order.bl.OrderBL;
import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/public/order") // FIXME: No todos los endpoints deberían ser públicos
public class OrderAPI {
    
    Logger LOGGER = LoggerFactory.getLogger(OrderAPI.class);

    @Autowired
    private OrderBL orderBL;

    @Autowired
    private UserBL userBL;

    @Autowired
    private CustomerBL customerBL;

    @Operation(summary = "List orders by datetime", description = "Can page through orders by datetime, the displayed orders are the most recent ones. No filters are applied at this moment.")
    @GetMapping
    public ResponseEntity<SyntaxFlavorResponse<Page<OrderDTO>>> listOrdersByDatetime(@RequestParam int pageNumber) {
        LOGGER.info("Endpoint GET /api/v1/order with pageNumber: {}", pageNumber);
        // TODO: Cuando se tenga el middleware de jwt, se debe extraer el userId del token antes de llamar al endopoint
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

    @Operation(summary = "List orders by status", description = "Can page through orders by status, the displayed orders are the most recent ones. No filters are applied at this moment.")
    @GetMapping("/status")
    public ResponseEntity<SyntaxFlavorResponse<Page<OrderDTO>>> listOrdersByStatus(@RequestParam int pageNumber, @RequestParam String status) {
        LOGGER.info("Endpoint GET /api/v1/order/status with pageNumber: {} and status: {}", pageNumber, status);
        // TODO: Cuando se tenga el middleware de jwt, se debe extraer el userId del token antes de llamar al endopoint
        SyntaxFlavorResponse<Page<OrderDTO>> sfr = new SyntaxFlavorResponse<>();
        try {
            Page<OrderDTO> orders = orderBL.listOrdersByStatus(pageNumber, status);
            sfr.setResponseCode("ORD-000");
            sfr.setPayload(orders);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @Operation(summary = "Create order from cart", description = "Creates an order from a cart. The customer ID is extracted from the JWT token, and a map of menu item IDs to quantities is passed.")
    @PostMapping
    public ResponseEntity<SyntaxFlavorResponse<CartDTO>> createOrderFromCart(
            @RequestBody CartDTO cart, HttpServletRequest request) {

        SyntaxFlavorResponse<CartDTO> sfr = new SyntaxFlavorResponse<>();
        LOGGER.info("Endpoint POST /api/v1/order with cart: {}", cart);
        // TODO: Cuando se tenga el middleware de jwt, se debe extraer el userId del token antes de llamar al endopoint
        try {
            // Extract JWT from Authorization header
            String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);  // Remove "Bearer " prefix from the token
            String kcUserId;

            try {
                kcUserId = JWT.decode(token).getSubject();  // Decode JWT to get the kcUserId
                LOGGER.info("Decoded kcUserId from JWT: {}", kcUserId);
            } catch (JWTDecodeException ex) {
                LOGGER.error("Invalid JWT token: {}", ex.getMessage());
                sfr.setResponseCode("ORD-602");
                sfr.setErrorMessage("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(sfr);  // Return 401 if the token is invalid
            }

            // Fetch the user by kcUserId
            User user = userBL.findUserByKcUserId(kcUserId);
            if (user == null) {
                LOGGER.error("User with kcUserId {} not found", kcUserId);
                sfr.setResponseCode("ORD-601");
                sfr.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfr);  // Return 404 if user is not found
            }

            // Fetch the customer associated with the user
            Customer customer = customerBL.findCustomerByUserId(user.getId());
            if (customer == null) {
                LOGGER.error("Customer for userId {} not found", user.getId());
                sfr.setResponseCode("ORD-602");
                sfr.setErrorMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfr);  // Return 404 if customer is not found
            }

            // Set the customerId in the CartDTO (we no longer take it from the request body)
            cart.setCustomerId(customer.getId());

            // Create the order using the updated CartDTO (with customerId set)
            CartDTO cartResponse = orderBL.createOrderFromCart(cart);
            sfr.setResponseCode("ORD-001");
            sfr.setPayload(cartResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);  // Return 201 Created if successful

        } catch (Exception e) {
            LOGGER.error("Error creating order: {}", e.getMessage());
            sfr.setResponseCode("ORD-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);  // Return 500 for any other errors
        }
    }

    @Operation(summary = "Cancel order", description = "Cancels an order by ID. Returns the order with the new status")
    @PutMapping("/cancel")
    public ResponseEntity<SyntaxFlavorResponse<OrderDTO>> cancelOrder(@RequestParam int orderId) {
        LOGGER.info("Endpoint PUT /api/v1/order/cancel with orderId: {}", orderId);
        SyntaxFlavorResponse<OrderDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            OrderDTO order = orderBL.setOrderStatusById(orderId, OrderBL.STATUS_CANCELLED);
            sfr.setResponseCode("ORD-002");
            sfr.setPayload(order);
            return ResponseEntity.ok(sfr);
        } catch (IllegalStateException e) {
            LOGGER.error("Failed to cancel order: {}", e.getMessage());
            sfr.setResponseCode("ORD-602");
            sfr.setErrorMessage("Failed to cancel order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-602");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @Operation(summary = "Deliver order", description = "Delivers an order by ID. Returns the order with the new status")
    @PutMapping("/deliver")
    public ResponseEntity<SyntaxFlavorResponse<OrderDTO>> deliverOrder(@RequestParam int orderId) {
        LOGGER.info("Endpoint PUT /api/v1/order/deliver with orderId: {}", orderId);
        SyntaxFlavorResponse<OrderDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            OrderDTO order = orderBL.setOrderStatusById(orderId, OrderBL.STATUS_DELIVERED);
            sfr.setResponseCode("ORD-003");
            sfr.setPayload(order);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("ORD-603");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @Operation(summary = "List orders by customer ID", description = "Lists the last 10 orders of a customer extracted from the JWT token")
    @GetMapping("/customer")
    public ResponseEntity<SyntaxFlavorResponse<List<OrderDTO>>> listOrdersByCustomerId(@RequestHeader("Authorization") String token) {
        // TODO: Add logger
        // FIXME: No es la mejor forma de manejar el token JWT. Ver de incluir middleware
        SyntaxFlavorResponse<List<OrderDTO>> sfr = new SyntaxFlavorResponse<>();
        try {
            // Extraer el kc_user_id del token JWT
            String kcUserId = JWT.decode(token.substring(7)).getSubject();
            LOGGER.info("Decoded kcUserId from JWT: {}", kcUserId);

            // Buscar el usuario por kc_user_id
            User user = userBL.findUserByKcUserId(kcUserId);

            // Obtener las órdenes del cliente usando el userId
            List<OrderDTO> orders = orderBL.listOrdersByCustomerId(user.getId());
            sfr.setResponseCode("ORD-000");
            sfr.setPayload(orders);
            return ResponseEntity.ok(sfr);
        } catch (Exception e) {
            LOGGER.error("Error listing orders: {}", e.getMessage());
            sfr.setResponseCode("ORD-600");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }
}
