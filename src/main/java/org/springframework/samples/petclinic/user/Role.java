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
    private String name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
