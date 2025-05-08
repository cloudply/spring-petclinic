package org.springframework.samples.petclinic.user;

import org.springframework.data.repository.Repository;

public interface RoleRepository extends Repository<Role, Integer> {

    void save(Role role);
}
