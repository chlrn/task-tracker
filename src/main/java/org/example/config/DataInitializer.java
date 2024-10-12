package org.example.config;


import lombok.RequiredArgsConstructor;
import org.example.entity.Role;
import org.example.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.findByName("ROLE_USER").isPresent()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }
        if (!roleRepository.findByName("ROLE_MANAGER").isPresent()) {
            roleRepository.save(new Role(null, "ROLE_MANAGER"));
        }
        if (!roleRepository.findByName("ROLE_ADMIN").isPresent()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
    }
}
