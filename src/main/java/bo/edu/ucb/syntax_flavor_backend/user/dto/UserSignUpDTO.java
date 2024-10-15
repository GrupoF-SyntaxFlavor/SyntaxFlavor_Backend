package bo.edu.ucb.syntax_flavor_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDTO {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @Null
    private String nit;
    @Null
    private String billName;
    @Null
    private String location;

    public UserSignUpDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }

}
