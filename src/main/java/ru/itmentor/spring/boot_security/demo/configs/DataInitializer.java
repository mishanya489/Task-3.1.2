package ru.itmentor.spring.boot_security.demo.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itmentor.spring.boot_security.demo.models.Role;
import ru.itmentor.spring.boot_security.demo.models.User;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmentor.spring.boot_security.demo.services.UserService;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final AdminProperties adminProperties;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        Set<Role> roles = adminProperties.getRoles().stream()
                .map(roleName -> {
                    String fullName = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
                    return roleRepository.findByName(fullName).orElseGet(() -> {
                        Role role = new Role();
                        role.setName(fullName);
                        return roleRepository.save(role);
                    });
                })
                .collect(Collectors.toSet());

        if (!userService.existsByUsername(adminProperties.getUsername())) {
            User admin = new User();
            admin.setUsername(adminProperties.getUsername());
            admin.setPassword(passwordEncoder.encode(adminProperties.getPassword()));
            admin.setEmail(adminProperties.getEmail());
            admin.setRoles(roles);

            userService.save(admin);
        }
    }
}