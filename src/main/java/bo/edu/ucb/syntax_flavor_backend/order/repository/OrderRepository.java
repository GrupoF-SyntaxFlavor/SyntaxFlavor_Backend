package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByOrderByOrderTimestampDesc(Pageable pageable);
    
}
