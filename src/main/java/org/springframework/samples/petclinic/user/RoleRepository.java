package org.springframework.samples.petclinic.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RoleRepository extends Repository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.username = :username")
    @Transactional(readOnly = true)
    List<Role> findByUsername(@Param("username") String username);

    void save(Role role);
}
