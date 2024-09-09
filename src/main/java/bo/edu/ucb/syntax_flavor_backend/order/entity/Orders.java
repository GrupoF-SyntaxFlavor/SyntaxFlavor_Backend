package bo.edu.ucb.syntax_flavor_backend.order.entity;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import bo.edu.ucb.syntax_flavor_backend.bill.entity.Bills;
import bo.edu.ucb.syntax_flavor_backend.user.entity.Customers;
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
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o"),
    @NamedQuery(name = "Orders.findById", query = "SELECT o FROM Orders o WHERE o.id = :id"),
    @NamedQuery(name = "Orders.findByOrderTimestamp", query = "SELECT o FROM Orders o WHERE o.orderTimestamp = :orderTimestamp"),
    @NamedQuery(name = "Orders.findByStatus", query = "SELECT o FROM Orders o WHERE o.status = :status"),
    @NamedQuery(name = "Orders.findByCreatedAt", query = "SELECT o FROM Orders o WHERE o.createdAt = :createdAt"),
    @NamedQuery(name = "Orders.findByUpdatedAt", query = "SELECT o FROM Orders o WHERE o.updatedAt = :updatedAt")})
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "order_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderTimestamp;
    @Column(name = "status")
    private String status;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(mappedBy = "orderId")
    private Collection<OrderItems> OrderItemsCollection;
    @OneToMany(mappedBy = "ordersId")
    private Collection<Bills> billsCollection;
    @JoinColumn(name = "custom", referencedColumnName = "id")
    @ManyToOne
    private Customers custom;

    public Orders() {
    }

    public Orders(Integer id) {
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

    public Collection<OrderItems> getOrderItemsCollection() {
        return OrderItemsCollection;
    }

    public void setOrderItemsCollection(Collection<OrderItems> OrderItemsCollection) {
        this.OrderItemsCollection = OrderItemsCollection;
    }

    public Collection<Bills> getBillsCollection() {
        return billsCollection;
    }

    public void setBillsCollection(Collection<Bills> billsCollection) {
        this.billsCollection = billsCollection;
    }

    public Customers getCustom() {
        return custom;
    }

    public void setCustom(Customers custom) {
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
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.condominio.tables_syntax_flavor.Orders[ id=" + id + " ]";
    }
    
}
