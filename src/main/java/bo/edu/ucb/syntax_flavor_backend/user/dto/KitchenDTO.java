package bo.edu.ucb.syntax_flavor_backend.user.dto;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Kitchen;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KitchenDTO {
    private Integer KitchenId;
    private String location;
    private UserDTO user;

    public KitchenDTO(Kitchen kitchen) {
        this.KitchenId = kitchen.getId();
        this.location = kitchen.getLocation();
        this.user = new UserDTO(kitchen.getUsersId());
    }
}
