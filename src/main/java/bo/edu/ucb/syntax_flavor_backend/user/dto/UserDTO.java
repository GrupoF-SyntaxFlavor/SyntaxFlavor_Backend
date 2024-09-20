package bo.edu.ucb.syntax_flavor_backend.user.dto;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer userId;
    private String name;
    private String email;
    private String phone;

    public UserDTO(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
    }
}
