package bo.edu.ucb.syntax_flavor_backend.order.entity;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bill;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customer;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "orders")
@NamedQueries({
    @NamedQuery(name = "Order.findAll", query = "SELECT o FROM Order o"),
    @NamedQuery(name = "Order.findById", query = "SELECT o FROM Order o WHERE o.id = :id"),
    @NamedQuery(name = "Order.findByOrderTimestamp", query = "SELECT o FROM Order o WHERE o.orderTimestamp = :orderTimestamp"),
    @NamedQuery(name = "Order.findByStatus", query = "SELECT o FROM Order o WHERE o.status = :status"),
    @NamedQuery(name = "Order.findByCreatedAt", query = "SELECT o FROM Order o WHERE o.createdAt = :createdAt"),
    @NamedQuery(name = "Order.findByUpdatedAt", query = "SELECT o FROM Order o WHERE o.updatedAt = :updatedAt")})
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "order_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderTimestamp = new Date();
    @Column(name = "table", nullable = true)//puede ser nulo
    private String table;
    @Column(name = "status")
    private String status;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    @OneToMany(mappedBy = "orderId")
    private Collection<OrderItem> OrderItemsCollection;
    @OneToMany(mappedBy = "ordersId")
    private Collection<Bill> billsCollection;
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ManyToOne
    private Customer custom;

    public Order() {
    }

    public Order(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(Date orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Collection<OrderItem> getOrderItemsCollection() {
        return OrderItemsCollection;
    }

    public void setOrderItemsCollection(Collection<OrderItem> OrderItemsCollection) {
        this.OrderItemsCollection = OrderItemsCollection;
    }

    public Collection<Bill> getBillsCollection() {
        return billsCollection;
    }

    public void setBillsCollection(Collection<Bill> billsCollection) {
        this.billsCollection = billsCollection;
    }

    public Customer getCustomerId() {
        return custom;
    }

    public void setCustomerId(Customer custom) {
        this.custom = custom;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Order)) {
            return false;
        }
        Order other = (Order) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.condominio.tables_syntax_flavor.Order[ id=" + id + " ]";
    }
    
}
