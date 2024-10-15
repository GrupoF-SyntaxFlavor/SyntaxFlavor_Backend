package bo.edu.ucb.syntax_flavor_backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.user.entity.Kitchen;

public interface KitchenRepository extends JpaRepository<Kitchen, Integer> {
    
}
