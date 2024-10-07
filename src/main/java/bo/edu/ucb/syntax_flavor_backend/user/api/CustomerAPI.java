package bo.edu.ucb.syntax_flavor_backend.user.api;

import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerUpdateDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

@RestController
@RequestMapping("/api/v1/user")
public class CustomerAPI {

    Logger LOGGER = LoggerFactory.getLogger(CustomerAPI.class);

    @Autowired
    private CustomerBL customerBL;

    @Autowired
    private UserBL userBL;

    @Operation(summary = "Get customer profile", description = "Returns the profile (name, email, NIT) of a customer using the kc_user_id from JWT token")
    @GetMapping("/customer/profile")
    public ResponseEntity<SyntaxFlavorResponse<CustomerDTO>> getCustomerProfile(@RequestHeader("Authorization") String token) {

        SyntaxFlavorResponse<CustomerDTO> response = new SyntaxFlavorResponse<>();
        try {
            // FIXME: No es la mejor forma de manejar tokens JWT.
            // TODO: Modularizar el manejo del JWT utilizando un middleware o una función auxiliar.
            String kcUserId = JWT.decode(token.substring(7)).getSubject();
            LOGGER.info("Endpoint GET /api/v1/user/customer/profile with kc_user_id extracted from token: {}", kcUserId);

            // Buscar el usuario en la tabla 'users' usando kc_user_id
            User user = userBL.findUserByKcUserId(kcUserId);  // Asegúrate de tener este método implementado en userBL

            if (user == null) {
                response.setResponseCode("ORD-601");
                response.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // FIXME: La lógica de negocio debería estar en el BL, no en la API.
            // TODO: Mover esta lógica al CustomerBL.
            Customer customer = customerBL.findCustomerByUserId(user.getId());

            if (customer == null) {
                response.setResponseCode("ORD-602");
                response.setErrorMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            CustomerDTO customerDTO = new CustomerDTO(customer);
            response.setResponseCode("ORD-000");
            response.setPayload(customerDTO);
            return ResponseEntity.ok(response);

        } catch (JWTDecodeException ex) {
            LOGGER.error("Invalid JWT token: {}", ex.getMessage());
            response.setResponseCode("ORD-603");
            response.setErrorMessage("Invalid JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            LOGGER.error("Error retrieving customer profile: {}", e.getMessage());
            response.setResponseCode("ORD-600");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Update customer data", description = "Updates the data of a customer using a CustomerUpdateDTO. All of the information in the body will override the data for the user.")
    @PatchMapping("/customer")
    public ResponseEntity<SyntaxFlavorResponse<CustomerDTO>> updateCustomerData(
            @RequestBody CustomerUpdateDTO customerUpdateDTO,
            HttpServletRequest request) {

        SyntaxFlavorResponse<CustomerDTO> response = new SyntaxFlavorResponse<>();
        LOGGER.info("Endpoint PATCH /api/v1/user/customer with customerUpdateDTO: {}", customerUpdateDTO);

        try {
            // FIXME: No es la mejor forma de manejar tokens JWT.
            // TODO: Modularizar el manejo del JWT utilizando un middleware o una función auxiliar.
            String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
            String kcUserId;
            try {
                kcUserId = JWT.decode(token).getSubject(); // Decode JWT to get userId
                LOGGER.info("Decoded kcUserId from JWT: {}", kcUserId);
            } catch (JWTDecodeException ex) {
                LOGGER.error("Invalid JWT token: {}", ex.getMessage());
                response.setResponseCode("ORD-602");
                response.setErrorMessage("Invalid JWT token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // FIXME: La lógica de negocio debería estar en el BL, no en la API.
            // TODO: Mover esta lógica al UserBL y CustomerBL.
            User user = userBL.findUserByKcUserId(kcUserId);
            if (user == null) {
                LOGGER.error("User with kcUserId {} not found", kcUserId);
                response.setResponseCode("ORD-601");
                response.setErrorMessage("User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Customer customer = customerBL.findCustomerByUserId(user.getId());
            if (customer == null) {
                LOGGER.error("Customer for userId {} not found", user.getId());
                response.setResponseCode("ORD-602");
                response.setErrorMessage("Customer not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // FIXME: La actualización del cliente también debería estar manejada en el BL.
            // TODO: Mover esta lógica al CustomerBL.
            Customer updatedCustomer = customerBL.updateCustomerData(customer, customerUpdateDTO);

            CustomerDTO customerDTO = new CustomerDTO(updatedCustomer);
            response.setResponseCode("ORD-002");
            response.setPayload(customerDTO);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            LOGGER.error("Error updating customer data: {}", e.getMessage());
            response.setResponseCode("ORD-602");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
