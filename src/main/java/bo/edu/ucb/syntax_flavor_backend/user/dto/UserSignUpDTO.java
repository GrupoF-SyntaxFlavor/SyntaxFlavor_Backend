package bo.edu.ucb.syntax_flavor_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSignUpDTO {
    /*
     * Esta clase se utiliza para crear usuarios tanto de tipo cliente como de tipo cocina
     * Por favor tener mucho cuidado con no cruzar los datos de los clientes con los de las cocinas
     */
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;

    // Customer only data
    private String nit;
    private String billName;

    // Kitchen only data
    private String location;


    public UserSignUpDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
    }

}