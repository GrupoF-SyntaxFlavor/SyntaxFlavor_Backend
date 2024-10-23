package bo.edu.ucb.syntax_flavor_backend.user.api;

import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerUpdateDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/user")
public class CustomerAPI {

    Logger LOGGER = LoggerFactory.getLogger(CustomerAPI.class);

    @Autowired
    private CustomerBL customerBL;

    @Autowired
    private UserBL userBL;

    @Operation(summary = "Get customer profile", description = "Returns the profile (name, email, NIT) of a customer using the kc_user_id from JWT token")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Customer profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Error retrieving customer profile")
        }
    )
    @GetMapping("/customer/profile")
    public ResponseEntity<SyntaxFlavorResponse<CustomerDTO>> getCustomerProfile(@RequestHeader("Authorization") String token, HttpServletRequest request) {

        SyntaxFlavorResponse<CustomerDTO> response = new SyntaxFlavorResponse<>();
        try {
            // Extraer el kc_user_id del token JWT
            String kcUserId = (String) request.getAttribute("kcUserId");
            LOGGER.info("Endpoint GET /api/v1/user/customer/profile with kc_user_id extracted from token: {}", kcUserId);

            // Buscar el usuario en la tabla 'users' usando kc_user_id
            User user = userBL.findUserByKcUserId(kcUserId);  // Asegúrate de tener este método implementado en userBL

            if (user == null) {
                response.setResponseCode("USR-601");
                response.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // FIXME: La lógica de negocio debería estar en el BL, no en la API.
            // TODO: Mover esta lógica al CustomerBL.
            Customer customer = customerBL.findCustomerByUserId(user.getId());

            if (customer == null) {
                response.setResponseCode("USR-602");
                response.setErrorMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            CustomerDTO customerDTO = new CustomerDTO(customer);
            response.setResponseCode("USR-000");
            response.setPayload(customerDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOGGER.error("Error retrieving customer profile: {}", e.getMessage());
            response.setResponseCode("USR-600");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Update customer data", description = "Updates the data of a customer using a CustomerUpdateDTO. All of the information in the body will override the data for the user.")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Customer data updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Error updating customer data")
        }
    )
    @PatchMapping("/customer")
    public ResponseEntity<SyntaxFlavorResponse<CustomerDTO>> updateCustomerData(
            @RequestBody CustomerUpdateDTO customerUpdateDTO,
            HttpServletRequest request) {

        SyntaxFlavorResponse<CustomerDTO> response = new SyntaxFlavorResponse<>();
        LOGGER.info("Endpoint PATCH /api/v1/user/customer with customerUpdateDTO: {}", customerUpdateDTO);

        try {

            String kcUserId = (String) request.getAttribute("kcUserId");
            User user = userBL.findUserByKcUserId(kcUserId);
            if (user == null) {
                LOGGER.error("User with kcUserId {} not found", kcUserId);
                response.setResponseCode("USR-601");
                response.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Customer customer = customerBL.findCustomerByUserId(user.getId());
            if (customer == null) {
                LOGGER.error("Customer for userId {} not found", user.getId());
                response.setResponseCode("USR-602");
                response.setErrorMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // FIXME: La actualización del cliente también debería estar manejada en el BL.
            // TODO: Mover esta lógica al CustomerBL.
            Customer updatedCustomer = customerBL.updateCustomerData(customer, customerUpdateDTO);

            CustomerDTO customerDTO = new CustomerDTO(updatedCustomer);
            response.setResponseCode("USR-002");
            response.setPayload(customerDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOGGER.error("Error updating customer data: {}", e.getMessage());
            response.setResponseCode("USR-602");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
