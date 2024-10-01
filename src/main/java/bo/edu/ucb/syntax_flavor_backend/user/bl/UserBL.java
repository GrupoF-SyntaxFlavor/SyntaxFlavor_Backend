package bo.edu.ucb.syntax_flavor_backend.user.bl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;




@Component
public class UserBL {
    Logger LOGGER = LoggerFactory.getLogger(UserBL.class);

    // @Autowired
    // private final KeycloakAdminClientService kcAdminClient;
    // private final KeycloakProvider kcProvider;

    // private UserRepository userRepository;

    // public UserDTO createUser(UserDTO user) throws RuntimeException {
    //     LOGGER.info("Creating user: {}", user);
    //     try {
            
    //         User userEntity = new User();
    //         userEntity.setName(user.getName());
    //         userEntity.setEmail(user.getEmail());
    //         userEntity.setPhone(user.getPhone());
    //         Timestamp currentlyDate = new Timestamp(System.currentTimeMillis());
    //         userEntity.setCreatedAt(currentlyDate);
    //         userEntity.setUpdatedAt(currentlyDate);
    //         Boolean isCreated  = keycloakBL.createUser(userEntity);
    //         if (!isCreated ) {
    //             throw new RuntimeException("Error creating user in keycloak");
    //         }
    //         User userResponse = userRepository.save(userEntity);
    //         return new UserDTO(userResponse);
    //     } catch (Exception e) {
    //         LOGGER.error("Error creating user: {}", e.getMessage());
    //         throw new RuntimeException("Error creating user: " + e.getMessage());
    //     }
    // }
}
