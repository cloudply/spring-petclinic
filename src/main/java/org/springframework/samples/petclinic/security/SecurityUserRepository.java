package org.springframework.samples.petclinic.security;

import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SecurityUserRepository extends Repository<SecurityUser, Integer> {

    @Transactional(readOnly = true)
    Optional<SecurityUser> findByUsername(String username);
}
