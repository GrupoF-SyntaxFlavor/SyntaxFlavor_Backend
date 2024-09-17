package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findAllByOrderByOrderTimestampDesc(Pageable pageable);
    
    Optional<Order> findById(Integer id);
}
