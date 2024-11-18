package bo.edu.ucb.syntax_flavor_backend.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Integer> {
    @Query("SELECT b FROM Bill b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Bill> findBillsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
