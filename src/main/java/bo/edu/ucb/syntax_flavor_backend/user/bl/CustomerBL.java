package bo.edu.ucb.syntax_flavor_backend.user.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import bo.edu.ucb.syntax_flavor_backend.user.dto.CustomerRequestDTO;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.user.repository.CustomerRepository;

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

    public Customer updateCustomerData(CustomerRequestDTO customer) throws RuntimeException {
        LOGGER.info("Updating customer data: {}", customer);
        try{
            Customer customerFound = customerRepository.findById(customer.getCustomerId()).orElse(null);
            if(customerFound == null) {
                LOGGER.error("No customer with provided ID was found");
                throw new RuntimeException("No customer with provided ID was found");
            }
            customerFound.setBillName(customer.getBillName());
            customerFound.setNit(customer.getNit());
            Customer updatedCustomer = customerRepository.save(customerFound);
            return updatedCustomer;
        }
        catch(Exception e) {
            LOGGER.error("Error updating customer data: {}", e.getMessage());
            throw new RuntimeException("Error updating customer data: " + e.getMessage());
        }
        
    }
}
