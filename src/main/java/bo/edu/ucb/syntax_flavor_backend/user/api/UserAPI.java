package bo.edu.ucb.syntax_flavor_backend.user.api;

import bo.edu.ucb.syntax_flavor_backend.user.bl.UserBL;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import bo.edu.ucb.syntax_flavor_backend.user.bl.CustomerBL;
import bo.edu.ucb.syntax_flavor_backend.user.bl.KitchenBL;
import bo.edu.ucb.syntax_flavor_backend.service.KeycloakAdminClientService;
import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserSignUpDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.KitchenDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.LoginDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorResponse;


import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserAPI {
    Logger LOGGER = LoggerFactory.getLogger(UserAPI.class);

    @Autowired
    private final KeycloakAdminClientService kcAdminClient;
    
    @Autowired
    private final CustomerBL customerBL;

    @Autowired
    private final KitchenBL kitchenBL;

    @Autowired
    private final UserBL userBL;

    public UserAPI(KeycloakAdminClientService kcAdminClient, CustomerBL customerBL, KitchenBL kitchenBL, UserBL userBL) {
        this.kcAdminClient = kcAdminClient;
        this.customerBL = customerBL;
        this.kitchenBL = kitchenBL;
        this.userBL = userBL;
    }

    @Operation(summary = "Create user", description = "Creates an user and saves data in keycloack realm. Data: id, name, email, number phone, date created at and date updated at.")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "User created successfully, be sure to validate the email"),
            @ApiResponse(responseCode = "400", description = "The request is missing a required parameter"),
            @ApiResponse(responseCode = "500", description = "Everything failed, the user was not created")
        }
    )
    @PostMapping("/public/signup")
    public ResponseEntity<SyntaxFlavorResponse<UserDTO>> createUser(
        @RequestBody UserSignUpDTO user,
        @RequestParam(value = "type", required = true) String type
    ) {
        LOGGER.info("Endpoint POST /api/v1/public/user with user: {}", user);
        
        // Depuración para validar que los valores no sean nulos
        LOGGER.debug("UserDTO received -  name: {}, email: {}", user.getName(), user.getEmail());

        SyntaxFlavorResponse<UserDTO> sfr = new SyntaxFlavorResponse<>();
        try {
            if (user.getName() == null || user.getEmail() == null) { //FIXME: Esta verificación debería hacerse dentro de createKeycloakUser, debe levantar un NullPointerException
                sfr.setResponseCode("USR-601");
                sfr.setErrorMessage("Email and Name are required to create a user in Keycloak.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sfr);
            }

            // TODO: esta funcionalidad debería moverse a un UserBL...
            UserDTO userResponse = kcAdminClient.createKeycloakUser(user.getName(), user.getEmail(), user.getPassword(), true);
            if(type.equals("customer")){
                CustomerDTO newCustomer = customerBL.createCustomer(userResponse, user.getNit(), user.getBillName());
                if(newCustomer == null)
                    throw new RuntimeException("Error creating customer");
            } else if (type.equals("kitchen")){
                KitchenDTO newKitchen = kitchenBL.createKitchen(userResponse, user.getLocation());
                if(newKitchen == null)
                    throw new RuntimeException("Error creating kitchen");
            } else {
                throw new RuntimeException("Invalid user type");
            }
            // ...hasta aquí


            sfr.setResponseCode("USR-001");
            sfr.setPayload(userResponse);
            return ResponseEntity.status(HttpStatus.CREATED).body(sfr);
        } catch (Exception e) { //TODO: Distinguir con excepciones específicas
            e.printStackTrace();
            sfr.setResponseCode("USR-601");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

    @Operation(summary = "Login user", description = "Login an user and compare data in keycloack realm. Data: username(email in keycloak), password.")
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "201", description = "User logged in successfully"),
            @ApiResponse(responseCode = "400", description = "The request is missing a required parameter"),
            @ApiResponse(responseCode = "500", description = "Everything failed, the user was not logged in")
        }
    )
    @PostMapping("/public/login")
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

    
    @Operation(
        summary = "List users with kitchens",
        description = "Lists all users who have a kitchen associated with them.",
        parameters = {
            @Parameter(name = "page", description = "Page number for pagination", example = "0"),
            @Parameter(name = "size", description = "Number of items per page", example = "10"),
            @Parameter(name = "sortBy", description = "Field to sort by", example = "id"),
            @Parameter(name = "sortOrder", description = "Sort order (asc or desc)", example = "asc")
        }
    )
    @ApiResponses(
        value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No users found with kitchens"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
        }
    )
    @GetMapping("/users-with-kitchen")
    public ResponseEntity<SyntaxFlavorResponse<List<UserDTO>>> listUsersWithKitchen(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        LOGGER.info("Endpoint GET /api/v1/public/users-with-kitchen");
    
        String kcUserId = (String) request.getAttribute("kcUserId");
    
        LOGGER.info("User with kcUserId {}", kcUserId);
        User user = userBL.findUserByKcUserId(kcUserId);
        // FIXME: this is not the best way to do it :)
        if (user == null) {
            LOGGER.error("User with kcUserId {} not found", kcUserId);
            SyntaxFlavorResponse<List<UserDTO>> sfrResponse = new SyntaxFlavorResponse<>();
            sfrResponse.setResponseCode("USR-601");
            sfrResponse.setErrorMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(sfrResponse);
        }
    
        SyntaxFlavorResponse<List<UserDTO>> sfr = new SyntaxFlavorResponse<>();
        try {
            List<UserDTO> usersWithKitchen = userBL.getUsersWithKitchen(page, size, sortBy, sortOrder);
            if (usersWithKitchen.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            sfr.setResponseCode("USR-002");
            sfr.setPayload(usersWithKitchen);
            return ResponseEntity.status(HttpStatus.OK).body(sfr);
        } catch (Exception e) {
            LOGGER.error("Error retrieving users with kitchens: {}", e.getMessage());
            sfr.setResponseCode("USR-602");
            sfr.setErrorMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sfr);
        }
    }

}