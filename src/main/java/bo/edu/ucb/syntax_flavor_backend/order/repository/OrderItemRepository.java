package bo.edu.ucb.syntax_flavor_backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    
}
