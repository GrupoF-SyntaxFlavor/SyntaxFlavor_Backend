package bo.edu.ucb.syntax_flavor_backend.menu.repository;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    List<MenuItem> findAllByOrderByNameAsc();

    //RETO
    Page<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
