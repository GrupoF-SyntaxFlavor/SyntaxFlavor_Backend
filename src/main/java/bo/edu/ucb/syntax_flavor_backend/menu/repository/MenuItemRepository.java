package bo.edu.ucb.syntax_flavor_backend.menu.repository;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {

    List<MenuItem> findAllByOrderByNameAsc();

    // RETO
    Page<MenuItem> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // New method with sorting by name
    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice ORDER BY m.name ASC")
    Page<MenuItem> findByPriceBetweenOrderByNameAsc(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT m FROM MenuItem m WHERE m.price BETWEEN :minPrice AND :maxPrice ORDER BY m.name DESC")
    Page<MenuItem> findByPriceBetweenOrderByNameDesc(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

}
