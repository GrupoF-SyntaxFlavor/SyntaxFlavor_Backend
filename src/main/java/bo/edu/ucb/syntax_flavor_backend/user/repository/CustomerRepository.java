package bo.edu.ucb.syntax_flavor_backend.user.repository;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Consulta derivada para encontrar un cliente por su users_id
    Customer findByUsersId(User user);
}
