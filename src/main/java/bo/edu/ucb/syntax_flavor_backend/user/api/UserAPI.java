package bo.edu.ucb.syntax_flavor_backend.user.api;

import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;
import bo.edu.ucb.syntax_flavor_backend.service.KeycloakAdminClientService;
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerSignUpDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.LoginDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class UserAPI {
    Logger LOGGER = LoggerFactory.getLogger(UserAPI.class);

    @Autowired
    private final KeycloakAdminClientService kcAdminClient;
    
    @Autowired
    private final CustomerBL customerBL;

    public UserAPI(KeycloakAdminClientService kcAdminClient, CustomerBL customerBL) {
        this.kcAdminClient = kcAdminClient;
        this.customerBL = customerBL;
    }

    @Operation(summary = "Create user", description = "Creates an user and saves data in keycloack realm. Data: id, name, email, number phone, date created at and date updated at.")
    @PostMapping("/public/signup")//public endpoint to create user
    public ResponseEntity<SyntaxFlavorResponse<UserDTO>> createUser(@RequestBody CustomerSignUpDTO user,
                                                                    @RequestParam(value = "type", required = true) String type) {
        LOGGER.info("Endpoint POST /api/v1/public/user with user: {}", user);
        
        // Depuración para validar que los valores no sean nulos
        LOGGER.debug("UserDTO received -  name: {}, email: {}", user.getName(), user.getEmail());

        SyntaxFlavorResponse<UserDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            if (user.getName() == null || user.getEmail() == null) {
                sfr.setResponseCode("USR-601");
                sfr.setErrorMessage("Email and Name are required to create a user in Keycloak.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfr);
            }

            UserDTO userResponse = kcAdminClient.createKeycloakUser(user.getName(), user.getEmail(), user.getPassword(), true);
            if(type.equals("customer")){
                CustomerDTO newCustomer = customerBL.createCustomer(userResponse, user.getNit(), user.getBillName());
                if(newCustomer == null)
                    throw new RuntimeException("Error creating customer");
            }
            sfr.setResponseCode("USR-001");
            sfr.setPayload(userResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("USR-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @Operation(summary = "Login user", description = "Login an user and compare data in keycloack realm. Data: username(email in keycloak), password.")
    @PostMapping("/public/login")//public endpoint to create user
    public ResponseEntity<SyntaxFlavorResponse<AccessTokenResponse>> login(@RequestBody LoginDTO login) {

        LOGGER.info("Endpoint POST /api/v1/public/login with login: {}", login);
        
        // Depuración para validar que los valores no sean nulos
        LOGGER.debug("LoginDTO received -  email: {}, password: {}", login.getEmail(), login.getPassword());//TODO que no se vea el password

        SyntaxFlavorResponse<AccessTokenResponse> sfr = new SyntaxFlavorResponse<>();
        try {
            if (login.getEmail() == null || login.getPassword() == null) {
                sfr.setResponseCode("USR-601");
                sfr.setErrorMessage("Email and Password are required to login in Keycloak.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfr);
            }

            AccessTokenResponse accessTokenResponse = kcAdminClient.login(login.getEmail(), login.getPassword());
            sfr.setResponseCode("USR-001");
            sfr.setPayload(accessTokenResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("USR-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @GetMapping("/public/dev/sync/users")
    public ResponseEntity<SyntaxFlavorResponse<String>> syncUsersToKeycloak() {
        // Invoking this method will sync all users from the database to keycloak
        try {
            String responseMessage = kcAdminClient.syncUsersToKeycloak();
            SyntaxFlavorResponse<String> response = new SyntaxFlavorResponse<>();
            response.setResponseCode("USR-001");
            response.setPayload(responseMessage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            SyntaxFlavorResponse<String> response = new SyntaxFlavorResponse<>();
            response.setResponseCode("USR-601");
            response.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
