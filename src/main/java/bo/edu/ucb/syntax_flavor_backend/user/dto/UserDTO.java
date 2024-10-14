package bo.edu.ucb.syntax_flavor_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull
    private Integer userId;
    @NotNull
    private String name;
    @NotNull
    private String email;

    public UserDTO(User user) {
        this.userId = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }
}
