package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = " SELECT * FROM Orders o WHERE o.order_timestamp BETWEEN :startOfDay AND :endOfDay AND o.status <> 'FINISHED';", nativeQuery = true)
    Page<Order> findOrdersFromToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);

    
    Optional<Order> findById(Integer id);
}
