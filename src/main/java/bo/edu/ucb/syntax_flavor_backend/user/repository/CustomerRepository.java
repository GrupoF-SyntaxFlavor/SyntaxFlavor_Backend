package bo.edu.ucb.syntax_flavor_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
}
