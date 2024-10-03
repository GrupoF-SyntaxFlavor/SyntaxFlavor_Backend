package bo.edu.ucb.syntax_flavor_backend.user.dto;

import jakarta.validation.constraints.NotNull;

public class LoginDTO {
    @NotNull
    private String email;
    @NotNull
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(@NotNull String email, @NotNull String password) {
        this.email = email;
        this.password = password;
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
