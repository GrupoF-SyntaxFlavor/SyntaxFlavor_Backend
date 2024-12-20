package bo.edu.ucb.syntax_flavor_backend.user.bl;

import bo.edu.ucb.syntax_flavor_backend.user.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import bo.edu.ucb.syntax_flavor_backend.user.repository.CustomerRepository;
import bo.edu.ucb.syntax_flavor_backend.user.repository.UserRepository;

@Component
public class CustomerBL {
    
    Logger LOGGER = LoggerFactory.getLogger(CustomerBL.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    public CustomerBL(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public Customer findCustomerById(Integer customerId) throws RuntimeException{
        LOGGER.info("Finding customer by id: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null) {
            LOGGER.error("No customer with provided ID was found");
            throw new RuntimeException("No customer with provided ID was found");
        }
        return customer;
    }

    public Customer updateCustomerData(Customer customer, CustomerUpdateDTO customerUpdateDTO) throws RuntimeException {
        LOGGER.info("Updating customer data for customerId: {}", customer.getId());
        try {
            // Update fields with the new data from DTO
            customer.setBillName(customerUpdateDTO.getBillName());
            customer.setNit(customerUpdateDTO.getNit());

            // Save updated customer to repository
            Customer updatedCustomer = customerRepository.save(customer);
            LOGGER.info("Customer data updated successfully for customerId: {}", customer.getId());
            return updatedCustomer;
        } catch (Exception e) {
            LOGGER.error("Error updating customer data: {}", e.getMessage());
            throw new RuntimeException("Error updating customer data: " + e.getMessage());
        }
    }

    public BillingInfoDTO getBillingInfo(Integer customerId) throws RuntimeException {
        LOGGER.info("Getting billing info for customer with ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null) {
            LOGGER.error("No customer with provided ID was found");
            throw new RuntimeException("No customer with provided ID was found");
        }
        BillingInfoDTO billingInfo = new BillingInfoDTO();
        billingInfo.setBillName(customer.getBillName());
        billingInfo.setNit(customer.getNit());
        return billingInfo;
    }

    public CustomerDTO createCustomer(UserDTO user, String nit, String billName) {
        LOGGER.info("Creating customer with user: {}", user);
        try{
            Customer newCustomer = new Customer();
            User localUser = userRepository.findById(user.getUserId()).orElseThrow(() -> new RuntimeException("No user with provided ID was found"));
            newCustomer.setUsersId(localUser);
            newCustomer.setBillName(billName);
            newCustomer.setNit(nit);
            Customer customer = customerRepository.save(newCustomer);
            return new CustomerDTO(customer);
        }
        catch(Exception e) {
            LOGGER.error("Error creating customer: {}", e.getMessage());
            throw new RuntimeException("Error creating customer: " + e.getMessage());
        }
        
    }

    public Customer findCustomerByUserId(Integer userId) throws RuntimeException {
        LOGGER.info("Finding customer by userId: {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            LOGGER.error("No user with provided ID was found");
            throw new RuntimeException("No user with provided ID was found");
        }
        Customer customer = customerRepository.findByUsersId(user).orElse(null);
        if (customer == null) {
            LOGGER.error("No customer with provided user ID was found");
            throw new RuntimeException("No customer with provided user ID was found");
        }
        return customer;
    }

    public Customer findCustomerByKcUserId(String kcUserId) throws RuntimeException {
        LOGGER.info("Finding customer by kcUserId: {}", kcUserId);
        User user = userRepository.findByKcUserId(kcUserId).orElse(null);
        if (user == null) {
            LOGGER.error("No user with provided ID was found");
            throw new RuntimeException("No user with provided ID was found");
        }
        LOGGER.info("User with id: {} found", user.getId());
        Customer customer = customerRepository.findByUsersId(user).orElse(null);
        if (customer == null) {
            LOGGER.error("No customer with provided user ID was found");
            throw new RuntimeException("No customer with provided user ID was found");
        }
        return customer;
    }

}
