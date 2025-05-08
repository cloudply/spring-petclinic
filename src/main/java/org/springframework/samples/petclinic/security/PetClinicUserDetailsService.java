package org.springframework.samples.petclinic.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetClinicUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public PetClinicUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        
        // For simplicity, we're hardcoding the role as OWNER
        // In a real application, you would fetch roles from the database
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), 
            user.getPassword(), 
            user.isEnabled(),
            true, // account not expired
            true, // credentials not expired
            true, // account not locked
            authorities
        );
    }
}
