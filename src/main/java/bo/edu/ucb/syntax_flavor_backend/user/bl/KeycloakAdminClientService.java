package bo.edu.ucb.syntax_flavor_backend.user.bl;

import java.util.Collections;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
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
    
    @Autowired
    private UserBL userBL;

    private final KeycloakProvider kcProvider;

    @PostConstruct
    public void init() {
        try {
            LOGGER.info("Synchronizing users with Keycloak...");
            synchronizeUsersWithKeycloak();
            LOGGER.info("User synchronization completed.");
        } catch (Exception e) {
            LOGGER.error("Error during user synchronization with Keycloak: {}", e.getMessage());
            // Manejo adicional de errores si es necesario
        }
    }

    public KeycloakAdminClientService(KeycloakProvider keycloakProvider, UserRepository userRepository) {
        this.kcProvider = keycloakProvider;
        this.userRepository = userRepository;
        
    }

    public UserDTO createKeycloakUser(UserRequestDTO user, Boolean inDB) {
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
        kcUser.setUsername(user.getEmail());
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
            if(!inDB){
                UserDTO userUpdated = userBL.setKeyCloakID(user.getId(), response);
                return userUpdated;
            }
            UserDTO userCreated = userBL.createUser(user, response);
            return userCreated;
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
    //copiar los 1000 usuarios en keycloak

    

    public void synchronizeUsersWithKeycloak() {
        LOGGER.info("Synchronizing users with Keycloak...");
        
        List<User> localUsers = userRepository.findAll(); // Obtener todos los usuarios de la base de datos local
        UsersResource usersResource = kcProvider.getInstance().realm(REALM).users();
        
        for (User localUser : localUsers) {
            // Verificar si el usuario ya existe en Keycloak
            if (localUser.getKcUserId() == null || !userExistsInKeycloak(usersResource, localUser.getKcUserId())) {
                // Crear el usuario en Keycloak
                UserRequestDTO userRequest = new UserRequestDTO();
                userRequest.setId(localUser.getId());
                userRequest.setEmail(localUser.getEmail());
                userRequest.setName(localUser.getName());
                userRequest.setPassword(localUser.getPassword()); //TODO que no se vea el password
                
                try {
                    createKeycloakUser(userRequest, false);
                    LOGGER.info("User {} created in Keycloak", localUser.getEmail());
                } catch (RuntimeException e) {
                    LOGGER.error("Failed to create user in Keycloak for email {}: {}", localUser.getEmail(), e.getMessage());
                }
            }
        }
    }

    private boolean userExistsInKeycloak(UsersResource usersResource, String kcUserId) {
        try {
            // Intentar obtener el usuario de Keycloak
            usersResource.get(kcUserId);
            return true; // Si no se lanza excepción, el usuario existe
        } catch (Exception e) {
            return false; // El usuario no existe
        }
    }

    public AccessTokenResponse login(String username, String password) {
        Client client = ClientBuilder.newClient();

        // Construimos el formulario con los datos del usuario
        Form form = new Form();
        form.param("client_id", "syntaxflavor"); // Tu client_id
        form.param("grant_type", "password");
        form.param("username", username);
        form.param("password", password);
        form.param("client_secret", "NmuoI18AX2WNcXwnWmScVGUdjC7gMFvr");

        // Realizamos la solicitud POST para obtener el token
        Response response = client
                .target("http://localhost:8082/realms/syntaxflavor_users/protocol/openid-connect/token")
                .request(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                .post(Entity.form(form));

        if (response.getStatus() == 200) {
            // Convertimos la respuesta en un AccessTokenResponse
            AccessTokenResponse tokenResponse = response.readEntity(AccessTokenResponse.class);
            return tokenResponse;
        } else {
            String errorMessage = response.readEntity(String.class);
            throw new RuntimeException("Failed to login with Keycloak: " + errorMessage);
        }
    }
}
