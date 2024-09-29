package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // @Query(value = " SELECT * FROM Orders o WHERE o.order_timestamp BETWEEN :startOfDay AND :endOfDay AND o.status <> 'FINISHED';", nativeQuery = true)
    // Page<Order> findOrdersFromToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);

    Page<Order> findAllByOrderTimestampBetweenOrderByOrderTimestampAsc(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    //incluyendo lo anterior añadir filtrar por estado, pero seguir mostrando la cantidad del pageable
    Page<Order> findAllByOrderTimestampBetweenAndStatusOrderByOrderTimestampAsc(LocalDateTime startOfDay, LocalDateTime endOfDay, String status, Pageable pageable);

    Optional<Order> findById(Integer id);

    //listar ultimas ordenes por id de cliente y ordenar por fecha de orden descendente
    List<Order> findAllByCustomIdOrderByOrderTimestampDesc(Integer customId, Pageable pageable);

    //select a utilizar: SELECT * FROM Orders o WHERE o.order_timestamp BETWEEN :startOfDay AND :endOfDay AND o.status = :status ORDER BY o.order_timestamp ASC;
    //ejemplo de select para probar en consulta sql: SELECT * FROM Orders o WHERE o.order_timestamp BETWEEN '2024-09-29 00:00:00' AND '2024-09-29 23:59:59' AND o.status = 'Cancelado' ORDER BY o.order_timestamp ASC;
    @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :startOfDay AND :endOfDay AND o.status = :status ORDER BY o.orderTimestamp ASC")
    Page<Order> findOrdersBetweenDatesFilteredByStatus(
        @Param("startOfDay") LocalDateTime startOfDay, 
        @Param("endOfDay") LocalDateTime endOfDay, 
        @Param("status") String status, 
        Pageable pageable
    );

}
