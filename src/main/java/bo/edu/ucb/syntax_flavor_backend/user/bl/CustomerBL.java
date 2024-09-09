package bo.edu.ucb.syntax_flavor_backend.user.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.user.repository.CustomerRepository;

public class CustomerBL {
    
    Logger LOGGER = LoggerFactory.getLogger(CustomerBL.class);

    @Autowired
    private CustomerRepository customerRepository;

    public Customer findCustomerById(Integer customerId) {
        LOGGER.info("Finding customer by id: {}", customerId);
        return customerRepository.findById(customerId).orElse(null);
    }
}
