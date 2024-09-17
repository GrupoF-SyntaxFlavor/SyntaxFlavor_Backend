package bo.edu.ucb.syntax_flavor_backend.bill.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Integer> {
    
}
