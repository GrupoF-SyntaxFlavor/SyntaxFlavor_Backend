package bo.edu.ucb.syntax_flavor_backend.order.api;

import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;
import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bo.edu.ucb.syntax_flavor_backend.order.bl.OrderBL;
import bo.edu.ucb.syntax_flavor_backend.order.dto.CartDTO;
import bo.edu.ucb.syntax_flavor_backend.order.dto.OrderDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/v1")
public class OrderAPI {

    Logger LOGGER = LoggerFactory.getLogger(OrderAPI.class);

    @Autowired
    private OrderBL orderBL;

    @Autowired
    private UserBL userBL;

    @Autowired
    private CustomerBL customerBL;

    @Operation(summary = "List orders by status and date range", description = "Retrieve a paginated list of orders filtered by status and date range. You can specify the order status, the minimum and maximum dates for the order timestamps, the page number, page size, and whether to sort by ascending or descending order based on the timestamp.")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of orders returned successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/order")
    public ResponseEntity<SyntaxFlavorResponse<Page<OrderDTO>>> listOrdersByFilters(
            @RequestParam(defaultValue = "Pendiente") String status,
            @RequestParam(required = false) LocalDateTime minDate,
            @RequestParam(required = false) LocalDateTime maxDate,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "true") boolean sortAscending) {
        LOGGER.info("Endpoint GET /api/v1/order with pageNumber: {}", pageNumber);
        SyntaxFlavorResponse<Page<OrderDTO>> sfr = new SyntaxFlavorResponse<>();
        minDate = minDate != null ? minDate.withHour(0).withMinute(0).withSecond(0).withNano(0) : null;
        maxDate = maxDate != null ? maxDate.withHour(23).withMinute(59).withSecond(59).withNano(999999999) : null;
        try {
            Page<OrderDTO> orders = orderBL.listOrdersByFilters(status, minDate, maxDate, pageNumber, pageSize,
                    sortAscending);
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
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "List of orders returned successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/order/status")
    public ResponseEntity<SyntaxFlavorResponse<Page<OrderDTO>>> listOrdersByStatus(@RequestParam int pageNumber,
            @RequestParam String status) {
        LOGGER.info("Endpoint GET /api/v1/order/status with pageNumber: {} and status: {}", pageNumber, status);
        // FIXED: Cuando se tenga el middleware de jwt, se debe extraer el userId del
        // token antes de llamar al endopoint
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
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Order created successfully"),
                    @ApiResponse(responseCode = "404", description = "User or customer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/order")
    public ResponseEntity<SyntaxFlavorResponse<CartDTO>> createOrderFromCart(
            @RequestBody CartDTO cart, HttpServletRequest request) {

        SyntaxFlavorResponse<CartDTO> sfr = new SyntaxFlavorResponse<>();
        LOGGER.info("Endpoint POST /api/v1/order with cart: {}", cart);
        // FIXED: Cuando se tenga el middleware de jwt, se debe extraer el userId del
        // token antes de llamar al endopoint
        try {

            // Fetch the user by kcUserId
            //FIXME: This should be done elsewhere, if the user is now authenticated a special exception should be thrown
            String kcUserId = (String) request.getAttribute("kcUserId");
            User user = userBL.findUserByKcUserId(kcUserId);
            if (user == null) {
                LOGGER.error("User with kcUserId {} not found", kcUserId);
                sfr.setResponseCode("ORD-601");
                sfr.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfr); // Return 404 if user is not found
            }

            // Fetch the customer associated with the user
            Customer customer = customerBL.findCustomerByUserId(user.getId());
            if (customer == null) {
                LOGGER.error("Customer for userId {} not found", user.getId());
                sfr.setResponseCode("ORD-602");
                sfr.setErrorMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfr); // Return 404 if customer is not found
            }

            // Set the customerId in the CartDTO (we no longer take it from the request
            // body)
            cart.setCustomerId(customer.getId());
            LOGGER.info("Creating order from cart: {}", cart);
            // Create the order using the updated CartDTO (with customerId set)
            CartDTO cartResponse = orderBL.createOrderFromCart(cart);
            sfr.setResponseCode("ORD-001");
            sfr.setPayload(cartResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr); // Return 201 Created if successful

        } catch (Exception e) {
            LOGGER.error("Error creating order: {}", e.getMessage());
            sfr.setResponseCode("ORD-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr); // Return 500 for any other errors
        }
    }

    @Operation(summary = "Cancel order", description = "Cancels an order by ID. Returns the order with the new status")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
                    @ApiResponse(responseCode = "400", description = "The order could not be cancelled, probably because it is already cancelled or delivered"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/order/cancel")
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
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Order delivered successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/order/deliver")
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
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of orders returned successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/order/customer")
    public ResponseEntity<SyntaxFlavorResponse<Page<OrderDTO>>> listOrdersByCustomerId(
            @RequestParam(defaultValue = "Pendiente") String status,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "true") boolean sortAscending,
            @RequestHeader("Authorization") String token, HttpServletRequest request) {

        SyntaxFlavorResponse<Page<OrderDTO>> sfr = new SyntaxFlavorResponse<>();
        try {
            LOGGER.info("Endpoint GET /api/v1/order/customer with pageNumber: {}", pageNumber);
            String kcUserId = (String) request.getAttribute("kcUserId");
            User user = userBL.findUserByKcUserId(kcUserId);
            // Get Customer ID
            Customer customer = customerBL.findCustomerByUserId(user.getId());
            // Obtener las órdenes del cliente usando el userId
            Page<OrderDTO> orders = orderBL.listOrdersByCustomerId(customer.getId(), status, pageNumber, pageSize,
                    sortAscending);
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
