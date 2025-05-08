package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.samples.petclinic.user.Role;
import org.springframework.samples.petclinic.user.RoleRepository;
import org.springframework.samples.petclinic.user.User;
import org.springframework.samples.petclinic.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        User adminUser = userRepository.findByUsername("admin");
        if (adminUser == null) {
            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
            
            Role adminRole = new Role();
            adminRole.setUsername("admin");
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        
        // Create owner user if it doesn't exist
        User ownerUser = userRepository.findByUsername("owner");
        if (ownerUser == null) {
            ownerUser = new User();
            ownerUser.setUsername("owner");
            ownerUser.setPassword(passwordEncoder.encode("owner"));
            ownerUser.setEnabled(true);
            userRepository.save(ownerUser);
            
            Role ownerRole = new Role();
            ownerRole.setUsername("owner");
            ownerRole.setName("ROLE_OWNER");
            roleRepository.save(ownerRole);
        }
        
        // Create vet user if it doesn't exist
        User vetUser = userRepository.findByUsername("vet");
        if (vetUser == null) {
            vetUser = new User();
            vetUser.setUsername("vet");
            vetUser.setPassword(passwordEncoder.encode("vet"));
            vetUser.setEnabled(true);
            userRepository.save(vetUser);
            
            Role vetRole = new Role();
            vetRole.setUsername("vet");
            vetRole.setName("ROLE_VET");
            roleRepository.save(vetRole);
        }
    }
}
