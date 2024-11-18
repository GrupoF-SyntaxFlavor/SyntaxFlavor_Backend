package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bo.edu.ucb.syntax_flavor_backend.menu.entity.MenuItem;
import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

        List<OrderItem> findByMenuItemId(MenuItem menuItemId);

        @Query("SELECT m.name AS name, SUM(oi.quantity) AS totalQuantity " +
                "FROM OrderItem oi " +
                "JOIN oi.menuItem m " +
                "JOIN oi.order o " +
                "WHERE o.orderTimestamp BETWEEN :startDate AND :endDate " +
                "GROUP BY m.name " +
                "ORDER BY SUM(oi.quantity) DESC")
        List<Object[]> findMostSoldMenuItems(
                @Param("startDate") Date startDate,
                @Param("endDate") Date endDate);

}
