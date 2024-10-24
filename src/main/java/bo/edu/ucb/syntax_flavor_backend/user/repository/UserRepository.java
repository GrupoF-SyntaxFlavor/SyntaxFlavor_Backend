package bo.edu.ucb.syntax_flavor_backend.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import bo.edu.ucb.syntax_flavor_backend.user.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String personalMail);
    Optional<User> findById(Integer id);

    Optional<User> findByKcUserId(String kcUserId);

    @Query("SELECT DISTINCT u FROM User u JOIN u.kitchenCollection k")
    Page<User> findUsersWithKitchen(Pageable pageable);
}