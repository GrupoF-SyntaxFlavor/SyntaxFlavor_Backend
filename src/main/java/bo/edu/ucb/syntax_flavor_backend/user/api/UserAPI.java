package bo.edu.ucb.syntax_flavor_backend.user.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bo.edu.ucb.syntax_flavor_backend.user.bl.KeycloakAdminClientService;
// import bo.edu.ucb.syntax_flavor_backend.user.bl.KeycloakProvider;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class UserAPI {
    Logger LOGGER = LoggerFactory.getLogger(UserAPI.class);

    @Autowired
    private final KeycloakAdminClientService kcAdminClient;
    // private final KeycloakProvider kcProvider;

    public UserAPI(KeycloakAdminClientService kcAdminClient) {
        this.kcAdminClient = kcAdminClient;
    }

    @Operation(summary = "Create user", description = "Creates an user and saves data in keycloack realm. Data: id, name, email, number phone, date created at and date updated at.")
    @PostMapping("/public/user")//public endpoint to create user
    public ResponseEntity<SyntaxFlavorResponse<UserDTO>> createUser(@RequestBody UserRequestDTO user) {
        LOGGER.info("Endpoint POST /api/v1/public/user with user: {}", user);
        
        // Depuración para validar que los valores no sean nulos
        LOGGER.debug("UserDTO received -  name: {}, email: {}, password: {}", user.getName(), user.getEmail(), user.getPassword());//TODO que no se vea el password

        SyntaxFlavorResponse<UserDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            if (user.getName() == null || user.getEmail() == null) {
                sfr.setResponseCode("USR-601");
                sfr.setErrorMessage("Email and Name are required to create a user in Keycloak.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfr);
            }

            UserDTO userResponse = kcAdminClient.createKeycloakUser(user);
            sfr.setResponseCode("USR-001");
            sfr.setPayload(userResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) {
            sfr.setResponseCode("USR-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }


}
