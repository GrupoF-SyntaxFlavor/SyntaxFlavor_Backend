package bo.edu.ucb.syntax_flavor_backend.bill.dto;

import java.math.BigDecimal;
import java.util.Date;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;

public class BillResponseDTO {
    private Integer billId;
    private Integer orderId;
    private String billName;
    private String nit;
    private BigDecimal totalCost;
    private Date createDate;

    public BillResponseDTO() {
    }

    public BillResponseDTO(Integer billId, Integer orderId, String billName, String nit, BigDecimal totalCost,
            Date createDate) {
        this.billId = billId;
        this.orderId = orderId;
        this.billName = billName;
        this.nit = nit;
        this.totalCost = totalCost;
        this.createDate = createDate;
    }

    public BillResponseDTO(Bill bill){
        this.billId = bill.getId();
        this.orderId = bill.getOrdersId().getId();
        this.billName = bill.getBillName();
        this.nit = bill.getNit();
        this.totalCost = bill.getTotalCost();
        this.createDate = bill.getCreatedAt();
    }

    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "BillResponseDTO [billId=" + billId + ", billName=" + billName + ", createDate=" + createDate + ", nit="
                + nit + ", orderId=" + orderId + ", totalCost=" + totalCost + "]";
    }
}
