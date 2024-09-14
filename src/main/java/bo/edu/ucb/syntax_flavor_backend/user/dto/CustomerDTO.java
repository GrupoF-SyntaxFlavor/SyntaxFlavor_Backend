package bo.edu.ucb.syntax_flavor_backend.user.dto;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Integer customerId;
    private String billName;
    private String nit;
    private UserDTO user;

    public CustomerDTO(Customer customer) {
        this.customerId = customer.getId();
        this.billName = customer.getBillName();
        this.nit = customer.getNit();
        this.user = new UserDTO(customer.getUsersId());
    }
}
