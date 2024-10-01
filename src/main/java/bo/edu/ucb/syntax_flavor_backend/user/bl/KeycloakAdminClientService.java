package bo.edu.ucb.syntax_flavor_backend.user.bl;

import java.util.Collections;
import java.sql.Timestamp;

import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import bo.edu.ucb.syntax_flavor_backend.user.repository.UserRepository;

@Service
public class KeycloakAdminClientService {

    Logger LOGGER = LoggerFactory.getLogger(KeycloakAdminClientService.class);

    @Value("${spring.keycloak.realm}")
    private String REALM;

    @Autowired
    private UserRepository userRepository;

    private final KeycloakProvider kcProvider;

    public KeycloakAdminClientService(KeycloakProvider keycloakProvider, UserRepository userRepository) {
        this.kcProvider = keycloakProvider;
        this.userRepository = userRepository;
    }

    public UserDTO createKeycloakUser(UserRequestDTO user) {
        LOGGER.info("Creating user in Keycloak Realm {}", REALM);
        
        // Verifica que el nombre no sea nulo antes de crear el usuario
        if (user.getEmail() == null || user.getName() == null) {
            throw new IllegalArgumentException("Email and Name are required to create a user in Keycloak.");
        }        
        
        UsersResource usersResource = kcProvider.getInstance().realm(REALM).users();
    
        // Generamos las credenciales de contraseña para el usuario
        LOGGER.info("Generating password credentials for user {}", user.getEmail());
        CredentialRepresentation credential = createPasswCredentials(user.getPassword());
    
        // Creamos la representación del usuario en Keycloak
        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getName()); // Usamos 'name' como 'username' en Keycloak
        kcUser.setCredentials(Collections.singletonList(credential));
        kcUser.setEmail(user.getEmail());
        kcUser.setFirstName(user.getName());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);
        // kcUser.setAttributes(Collections.singletonMap("phone", Collections.singletonList(user.getPhone())));
    
        LOGGER.info("Creating user");
        Response response = usersResource.create(kcUser);
        LOGGER.info("Received response from Keycloak with status {}", response.getStatus());
    
        if (response.getStatus() == 201) {
            LOGGER.info("User created in Keycloak, saving to local database");
            User localUser = new User();
            localUser.setName(user.getName());
            localUser.setEmail(user.getEmail());
            localUser.setPassword(user.getPassword());
            Timestamp currentlyDate = new Timestamp(System.currentTimeMillis());
            localUser.setCreatedAt(currentlyDate);
            localUser.setUpdatedAt(currentlyDate);
    
            // Extraemos el ID de usuario de Keycloak a partir de la respuesta
            String kcUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            localUser.setKcUserId(kcUserId);
    
            // Guardamos el usuario en la base de datos local
            User userCreated = userRepository.save(localUser);
            return new UserDTO(userCreated);
        } else {
            String errorMessage = response.readEntity(String.class);
            LOGGER.error("Failed to create user in Keycloak with status {} \n{}", response.getStatusInfo(), errorMessage);
            throw new RuntimeException("Failed to create user in Keycloak. Error: " + errorMessage);
        }
    }
    
    private static CredentialRepresentation createPasswCredentials(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }
}
