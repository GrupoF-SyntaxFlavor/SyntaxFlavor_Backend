package bo.edu.ucb.syntax_flavor_backend.user.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.user.repository.CustomerRepository;

@Component
public class CustomerBL {
    
    Logger LOGGER = LoggerFactory.getLogger(CustomerBL.class);

    @Autowired
    private CustomerRepository customerRepository;

    public Customer findCustomerById(Integer customerId) throws RuntimeException{
        LOGGER.info("Finding customer by id: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null) {
            LOGGER.error("No customer with provided ID was found");
            throw new RuntimeException("No customer with provided ID was found");
        }
        return customer;
    }
}
