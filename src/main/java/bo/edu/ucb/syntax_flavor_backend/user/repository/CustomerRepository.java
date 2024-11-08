package bo.edu.ucb.syntax_flavor_backend.user.repository;

import bo.edu.ucb.syntax_flavor_backend.user.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // Consulta derivada para encontrar un cliente por su users_id
    Optional<Customer> findByUsersId(User user);
}
