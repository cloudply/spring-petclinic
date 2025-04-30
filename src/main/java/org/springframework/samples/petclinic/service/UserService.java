package org.springframework.samples.petclinic.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        try {
            // Fetch roles from the database
            jdbcTemplate.query(
                "SELECT role FROM roles WHERE username=?", 
                new Object[]{username},
                (rs, rowNum) -> authorities.add(new SimpleGrantedAuthority(rs.getString("role")))
            );
            
            // If no roles found, add a default role
            if (authorities.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            }
        } catch (Exception e) {
            // Fallback in case of database issues
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), 
            user.getPassword(), 
            user.isEnabled(), 
            true, // account not expired
            true, // credentials not expired
            true, // account not locked
            authorities);
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
