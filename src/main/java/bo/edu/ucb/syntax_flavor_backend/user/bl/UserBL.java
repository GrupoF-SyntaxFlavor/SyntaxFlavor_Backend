package bo.edu.ucb.syntax_flavor_backend.user.bl;


import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import bo.edu.ucb.syntax_flavor_backend.user.repository.UserRepository;
import bo.edu.ucb.syntax_flavor_backend.util.SyntaxFlavorHash;
import jakarta.ws.rs.core.Response;




@Component
public class UserBL {
    Logger LOGGER = LoggerFactory.getLogger(UserBL.class);

    // @Autowired
    // private final KeycloakAdminClientService kcAdminClient;
    // private final KeycloakProvider kcProvider;

    @Autowired
    private UserRepository userRepository;

    public UserDTO createUser(UserRequestDTO user, Response response) throws RuntimeException {
        LOGGER.info("Creating user: {}", user);
        try {
            User localUser = new User();
            localUser.setName(user.getName());
            localUser.setEmail(user.getEmail());
            SyntaxFlavorHash hashUtil = new SyntaxFlavorHash();
            localUser.setPassword(hashUtil.hashPassword(user.getPassword()));//hasheando contraseña
            Timestamp currentlyDate = new Timestamp(System.currentTimeMillis());
            localUser.setCreatedAt(currentlyDate);
            localUser.setUpdatedAt(currentlyDate);
    
            // Extraemos el ID de usuario de Keycloak a partir de la respuesta
            String kcUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            localUser.setKcUserId(kcUserId);
    
            // Guardamos el usuario en la base de datos local
            User userCreated = userRepository.save(localUser);
            return new UserDTO(userCreated);
        } catch (Exception e) {
            LOGGER.error("Error creating user in local DB: {}", e.getMessage());
            throw new RuntimeException("Error creating user in local DB: " + e.getMessage());
        }
    }

    public UserDTO setKeyCloakID(Integer userId, Response response) throws RuntimeException {
        LOGGER.info("Setting Keycloak ID user: {}", userId);
        try {
            User localUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            Timestamp currentlyDate = new Timestamp(System.currentTimeMillis());
            localUser.setUpdatedAt(currentlyDate);
    
            // Extraemos el ID de usuario de Keycloak a partir de la respuesta
            String kcUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            localUser.setKcUserId(kcUserId);
            SyntaxFlavorHash hashUtil = new SyntaxFlavorHash();
            localUser.setPassword(hashUtil.hashPassword(localUser.getPassword()));//hasheando contraseña
            // Guardamos el usuario en la base de datos local
            User userUpdated = userRepository.save(localUser);
            return new UserDTO(userUpdated);
        } catch (Exception e) {
            LOGGER.error("Error updating user in local DB: {}", e.getMessage());
            throw new RuntimeException("Error updating user in local DB: " + e.getMessage());
        }
    }
}
