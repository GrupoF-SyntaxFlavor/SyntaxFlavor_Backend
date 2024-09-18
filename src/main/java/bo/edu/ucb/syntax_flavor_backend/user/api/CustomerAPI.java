package bo.edu.ucb.syntax_flavor_backend.user.api;

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
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/v1/user")
public class CustomerAPI {
    
    Logger LOGGER = LoggerFactory.getLogger(CustomerAPI.class);

    @Autowired
    private CustomerBL customerBL;
    
    @Operation(summary = "Get customer profile", description = "Returns the profile (name, email, NIT) of a customer given its ID as a")
    @GetMapping("/customer/profile")
    public ResponseEntity<SyntaxFlavorResponse<CustomerDTO>> getCustomerProfile(@RequestHeader ("customerId") Integer customerId) {
        SyntaxFlavorResponse<CustomerDTO> response = new SyntaxFlavorResponse<>();
        LOGGER.info("Endpoint GET /api/v1/user/customer/profile with customerId: {}", customerId);
        try {
            Customer customer = customerBL.findCustomerById(customerId);
            CustomerDTO customerDTO = new CustomerDTO(customer);
            response.setResponseCode("ORD-000");
            response.setPayload(customerDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setResponseCode("ORD-600");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "Update customer data", description = "Updates the data of a customer using a CustomerRequestDTO all of the information in the body will override the data for the user")
    @PatchMapping("/customer") 
    public ResponseEntity<SyntaxFlavorResponse<CustomerDTO>> updateCustomerData(@RequestBody CustomerRequestDTO customerRequestDTO) {
        SyntaxFlavorResponse<CustomerDTO> response = new SyntaxFlavorResponse<>();
        LOGGER.info("Endpoint PATCH /api/v1/user/customer with customerRequestDTO: {}", customerRequestDTO);
        try {
            Customer customer = customerBL.updateCustomerData(customerRequestDTO);
            CustomerDTO customerDTO = new CustomerDTO(customer);
            response.setResponseCode("ORD-002");
            response.setPayload(customerDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setResponseCode("ORD-602");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}
