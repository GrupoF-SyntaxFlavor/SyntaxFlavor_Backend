package bo.edu.ucb.syntax_flavor_backend.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    
}
