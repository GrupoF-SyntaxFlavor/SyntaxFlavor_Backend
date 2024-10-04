package bo.edu.ucb.syntax_flavor_backend.user.dto;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUpdateDTO {
    private String billName;
    private String nit;
    public CustomerUpdateDTO(Customer customer) {
        this.billName = customer.getBillName();
        this.nit = customer.getNit();
    }
}
