package bo.edu.ucb.syntax_flavor_backend.user.dto;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;
// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.NoArgsConstructor;

public class UserRequestDTO {
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String password;

    public UserRequestDTO() {

    }

    public UserRequestDTO( String name, String email, String password ) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public UserRequestDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
