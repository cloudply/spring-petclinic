package org.springframework.samples.petclinic.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.samples.petclinic.model.BaseEntity;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Column(name = "username")
    @NotEmpty
    private String username;

    @Column(name = "role")
    @NotEmpty
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
