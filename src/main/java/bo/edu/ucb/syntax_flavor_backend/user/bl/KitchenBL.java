package bo.edu.ucb.syntax_flavor_backend.user.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.user.dto.KitchenDTO;
import bo.edu.ucb.syntax_flavor_backend.user.dto.UserDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Kitchen;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import bo.edu.ucb.syntax_flavor_backend.user.repository.KitchenRepository;
import bo.edu.ucb.syntax_flavor_backend.user.repository.UserRepository;

@Component
public class KitchenBL {

    Logger LOGGER = LoggerFactory.getLogger(CustomerBL.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KitchenRepository kitchenRepository;

    public KitchenBL(UserRepository userRepository, KitchenRepository kitchenRepository) {
        this.userRepository = userRepository;
        this.kitchenRepository = kitchenRepository;
    }

    public KitchenDTO createKitchen(UserDTO user, String location) {
        try {
            LOGGER.info("Creating kitchen for user: {}", user.getEmail());
            User localUser = userRepository.findById(user.getUserId())
                    .orElseThrow(() -> new RuntimeException("No user with provided ID was found"));

            Kitchen newKitchen = new Kitchen();
            newKitchen.setLocation(location);
            newKitchen.setUsersId(localUser);

            kitchenRepository.save(newKitchen);
            LOGGER.info("Kitchen created successfully for user: {}", user.getEmail());

            return new KitchenDTO();
        } catch (Exception e) {
            LOGGER.error("Error creating kitchen: {}", e.getMessage());
            throw new RuntimeException("Error creating kitchen: " + e.getMessage());
        }
    }
}
