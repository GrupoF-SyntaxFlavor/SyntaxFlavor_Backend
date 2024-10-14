package bo.edu.ucb.syntax_flavor_backend.bill.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillResponseDTO {
    private Integer billId;
    private Integer orderId;
    private String billName;
    private String nit;
    private BigDecimal totalCost;
    private Date createDate;

    public BillResponseDTO(Bill bill) {
        this.billId = bill.getId();
        this.orderId = bill.getOrdersId().getId();
        this.billName = bill.getBillName();
        this.nit = bill.getNit();
        this.totalCost = bill.getTotalCost();
        this.createDate = bill.getCreatedAt();
    }

}
