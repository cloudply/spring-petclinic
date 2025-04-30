package org.springframework.samples.petclinic.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.samples.petclinic.model.User;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends Repository<User, Integer> {

    @Query("SELECT user FROM User user WHERE user.username = :username")
    @Transactional(readOnly = true)
    User findByUsername(@Param("username") String username);

    void save(User user);
}
