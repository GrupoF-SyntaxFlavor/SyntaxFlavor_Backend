package bo.edu.ucb.syntax_flavor_backend.user.entity;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.Serializable;
import java.util.Date;

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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 *
 * @author Usuario
 */
@Entity
@Table(name = "kitchens")
@NamedQueries({
    @NamedQuery(name = "Kitchen.findAll", query = "SELECT c FROM Kitchen c"),
    @NamedQuery(name = "Kitchen.findById", query = "SELECT c FROM Kitchen c WHERE c.id = :id"),
    @NamedQuery(name = "Kitchen.findByLocation", query = "SELECT c FROM Kitchen c WHERE c.location = :location"),
    @NamedQuery(name = "Kitchen.findByCreatedAt", query = "SELECT c FROM Kitchen c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "Kitchen.findByUpdatedAt", query = "SELECT c FROM Kitchen c WHERE c.updatedAt = :updatedAt")})
public class Kitchen implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "location")
    private String location;
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    @ManyToOne
    private User usersId;

    public Kitchen() {
    }

    public Kitchen(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public User getUsersId() {
        return usersId;
    }

    public void setUsersId(User usersId) {
        this.usersId = usersId;
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
        if (!(object instanceof Kitchen)) {
            return false;
        }
        Kitchen other = (Kitchen) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.condominio.tables_syntax_flavor.Kitchen[ id=" + id + " ]";
    }
    
}

