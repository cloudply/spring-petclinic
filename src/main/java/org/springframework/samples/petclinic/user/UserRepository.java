package org.springframework.samples.petclinic.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends Repository<User, Integer> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    @Transactional(readOnly = true)
    User findByUsername(@Param("username") String username);

    void save(User user);
}
