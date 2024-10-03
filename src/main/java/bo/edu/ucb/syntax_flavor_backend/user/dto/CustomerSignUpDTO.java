package bo.edu.ucb.syntax_flavor_backend.user.dto;


import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import jakarta.validation.constraints.NotNull;

// import lombok.NoArgsConstructor;

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

    public CustomerSignUpDTO() {

    }
    
    public CustomerSignUpDTO(String name, String email, String password, String nit, String billName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.nit = nit;
        this.billName = billName;
    }

    public CustomerSignUpDTO(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }
}
