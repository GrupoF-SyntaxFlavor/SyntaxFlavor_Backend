package bo.edu.ucb.syntax_flavor_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSignUpDTO {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nit;
    @NotNull
    private String billName;

    public CustomerSignUpDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }

}
