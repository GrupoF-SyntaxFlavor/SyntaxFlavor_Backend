package bo.edu.ucb.syntax_flavor_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Métodos personalizados aquí si los tienes
}
