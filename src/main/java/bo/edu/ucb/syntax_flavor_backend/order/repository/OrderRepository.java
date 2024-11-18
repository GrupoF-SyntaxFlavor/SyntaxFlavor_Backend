package bo.edu.ucb.syntax_flavor_backend.order.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
//import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

import bo.edu.ucb.syntax_flavor_backend.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    Page<Order> findAllByOrderTimestampBetweenOrderByOrderTimestampAsc(LocalDateTime startOfDay, LocalDateTime endOfDay,
            Pageable pageable);

    Optional<Order> findById(Integer id);

    // listar ultimas ordenes por id de cliente y ordenar por fecha de orden
    // descendente
    // List<Order> findAllByCustomIdOrderByOrderTimestampDesc(Integer customId,
    // Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.custom.id = :customerId " +
            "AND (:status IS NULL OR o.status = :status) ")
    Page<Order> findAllByCustomerIdAndStatusOrderByOrderTimestamp(Integer customerId, String status, Pageable pageable);

    // filtrar por usuario sin importar el status, ordenar por fecha
    @Query("SELECT o FROM Order o WHERE o.custom.id = :customerId ORDER BY o.orderTimestamp ASC")
        Page<Order> findAllByCustomerIdOrderByOrderTimestamp(Integer customerId, Pageable pageable);

    // filtrar por status
    Page<Order> findAllByOrderTimestampBetweenAndStatusOrderByOrderTimestampAsc(LocalDateTime startOfDay,
            LocalDateTime endOfDay, String status, Pageable pageable);

    // filtrar por status, fechas y ordenar por fecha
    @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :minDate AND :maxDate AND o.status = :status")
    Page<Order> findByMinDateBetweenMaxDateAndStatusByNameAsc(String status, LocalDateTime minDate,
            LocalDateTime maxDate, Pageable pageable);

        // filtrar solo por fechas y ordenar por fecha
        @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :minDate AND :maxDate")
        Page<Order> findByMinDateBetweenMaxDate(LocalDateTime minDate, LocalDateTime maxDate, Pageable pageable); 
        
        @Query("SELECT o FROM Order o WHERE o.orderTimestamp BETWEEN :minDate AND :maxDate")
        List<Order> findByMinDateBetweenMaxDateList(LocalDateTime minDate, LocalDateTime maxDate);            

}
