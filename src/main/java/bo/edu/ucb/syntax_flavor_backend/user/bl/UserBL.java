package bo.edu.ucb.syntax_flavor_backend.user.bl;


import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import bo.edu.ucb.syntax_flavor_backend.user.dto.KitchenDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserKitchenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public User findUserByKcUserId(String kcUserId) throws RuntimeException {
        LOGGER.info("Finding user by kcUserId: {}", kcUserId);
        User user = userRepository.findByKcUserId(kcUserId).orElse(null);
        if (user == null) {
            LOGGER.error("No user with provided Keycloak user ID was found");
            throw new RuntimeException("No user with provided Keycloak user ID was found");
        }
        return user;
    }
    public UserDTO getUserByEmail(String email) {
        LOGGER.warn("Person email: {}", email);
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null ? new UserDTO(user) : null;
    }

    public Page<UserKitchenDTO> getUsersWithKitchen(int page, int size, String sortBy, String sortOrder) {
        LOGGER.info("Listing users with kitchens");
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortBy));
        Page<User> userPage = userRepository.findUsersWithKitchen(pageable);

        return userPage.map(user -> {
            List<KitchenDTO> kitchenDTOs = user.getKitchenCollection().stream()
                    .map(kitchen -> new KitchenDTO(kitchen.getId(), kitchen.getLocation(), new UserDTO(user.getId(), user.getName(), user.getEmail())))
                    .collect(Collectors.toList());

            return new UserKitchenDTO(user.getId(), user.getName(), user.getEmail(), kitchenDTOs);
        });
    }

}
