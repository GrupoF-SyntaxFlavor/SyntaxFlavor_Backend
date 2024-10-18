package bo.edu.ucb.syntax_flavor_backend.menu.entity;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import bo.edu.ucb.syntax_flavor_backend.order.entity.OrderItem;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "MenuItems")
@NamedQueries({
        @NamedQuery(name = "MenuItem.findAll", query = "SELECT m FROM MenuItem m"),
        @NamedQuery(name = "MenuItem.findById", query = "SELECT m FROM MenuItem m WHERE m.id = :id"),
        @NamedQuery(name = "MenuItem.findByName", query = "SELECT m FROM MenuItem m WHERE m.name = :name"),
        @NamedQuery(name = "MenuItem.findByDescription", query = "SELECT m FROM MenuItem m WHERE m.description = :description"),
        @NamedQuery(name = "MenuItem.findByPrice", query = "SELECT m FROM MenuItem m WHERE m.price = :price"),
        @NamedQuery(name = "MenuItem.findByCreatedAt", query = "SELECT m FROM MenuItem m WHERE m.createdAt = :createdAt"),
        @NamedQuery(name = "MenuItem.findByUpdatedAt", query = "SELECT m FROM MenuItem m WHERE m.updatedAt = :updatedAt") })
public class MenuItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    // @Max(value=?) @Min(value=?)//if you know range of your decimal fields
    // consider using these annotations to enforce field validation
    @Basic(optional = false)
    @Column(name = "price")
    private BigDecimal price;
    @Basic(optional = true)
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "status")
    private Boolean status = true;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    @OneToMany(mappedBy = "menuItemId")
    private Collection<OrderItem> OrderItemsCollection;

    public MenuItem() {
    }

    public MenuItem(Integer id) {
        this.id = id;
    }

    public MenuItem(Integer id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MenuItem)) {
            return false;
        }
        MenuItem other = (MenuItem) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.condominio.tables_syntax_flavor.MenuItem[ id=" + id + " ]";
    }

}
