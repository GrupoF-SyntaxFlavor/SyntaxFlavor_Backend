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
    private KitchenRepository kitchenRepository;

    @Autowired
    private UserRepository userRepository;

    public KitchenBL(KitchenRepository kitchenRepository, UserRepository userRepository) {
        this.kitchenRepository = kitchenRepository;
        this.userRepository = userRepository;
    }

    public KitchenDTO createKitchen(UserDTO user, String location) {
        LOGGER.info("Creating kitchen with user: {}", user);
        try{
            Kitchen newKitchen = new Kitchen();
            User localUser = userRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("No user with provided ID was found"));
            newKitchen.setUsersId(localUser);
            newKitchen.setLocation(location);
            Kitchen kitchen = kitchenRepository.save(newKitchen);
            return new KitchenDTO(kitchen);
        }
        catch(Exception e) {
            LOGGER.error("Error creating kitchen: {}", e.getMessage());
            throw new RuntimeException("Error creating kitchen: " + e.getMessage());
        }
        
    }
}
