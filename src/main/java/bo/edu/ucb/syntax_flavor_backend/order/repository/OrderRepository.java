package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    // @Query(value = " SELECT * FROM Orders o WHERE o.order_timestamp BETWEEN :startOfDay AND :endOfDay AND o.status <> 'FINISHED';", nativeQuery = true)
    // Page<Order> findOrdersFromToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay, Pageable pageable);

    Page<Order> findAllByOrderTimestampBetweenOrderByOrderTimestampAsc(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    Optional<Order> findById(Integer id);

    //listar ultimas ordenes por id de cliente y ordenar por fecha de orden descendente
    List<Order> findAllByCustomIdOrderByOrderTimestampDesc(Integer customId, Pageable pageable);

    // @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :startOfDay AND :endOfDay AND o.status = :status ORDER BY o.orderTimestamp ASC")
    // Page<Order> findOrdersBetweenDatesFilteredByStatus(
    //     @Param("startOfDay") LocalDateTime startOfDay, 
    //     @Param("endOfDay") LocalDateTime endOfDay, 
    //     @Param("status") String status, 
    //     Pageable pageable
    // );

    //filtrar por status
    Page<Order> findAllByOrderTimestampBetweenAndStatusOrderByOrderTimestampAsc(LocalDateTime startOfDay, LocalDateTime endOfDay, String status, Pageable pageable);

    //filtrar por status, fechas y ordenar por fecha
    @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :minDate AND :maxDate AND o.status = :status")
    Page<Order> findByMinDateBetweenMaxDateAndStatusByNameAsc(String status, LocalDateTime minDate, LocalDateTime maxDate, Pageable pageable);

}
