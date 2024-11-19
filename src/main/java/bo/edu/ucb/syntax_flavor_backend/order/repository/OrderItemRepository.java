package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

        List<OrderItem> findByMenuItemId(MenuItem menuItemId);

        @Query("SELECT m.id AS menuItemId, m.name AS menuItemName, SUM(oi.price) AS totalPrice, SUM(oi.quantity) AS totalQuantity " +
                "FROM OrderItem oi JOIN oi.menuItemId m " +
                "WHERE oi.orderId.orderTimestamp BETWEEN :startDate AND :endDate " +
                "GROUP BY m.id, m.name " +
                "ORDER BY totalQuantity DESC")
        List<Object[]> findMostSoldMenuItems(
                @Param("startDate") LocalDateTime startDate, 
                @Param("endDate") LocalDateTime endDate,
                Pageable pageable);

}
