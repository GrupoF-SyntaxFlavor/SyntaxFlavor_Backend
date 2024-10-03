package bo.edu.ucb.syntax_flavor_backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String personalMail);
    Optional<User> findById(Integer id);
}
