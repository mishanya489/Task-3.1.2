package ru.itmentor.spring.boot_security.demo.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.dto.UserRegistrationDto;
import ru.itmentor.spring.boot_security.demo.models.Role;
import ru.itmentor.spring.boot_security.demo.models.User;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public void registerNewUser(UserRegistrationDto userRegistrationDto, Set<Long> roleIds) {
        User user = new User();
        user.setUsername(userRegistrationDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationDto.getPassword()));
        user.setEmail(userRegistrationDto.getEmail());

        // Получаем выбранные роли из базы
        Set<Role> roles = roleRepository.findAllById(roleIds).stream()
                .collect(Collectors.toSet());

        // Если роли не выбраны, добавляем USER по умолчанию
        if (roles.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("Default role ROLE_USER not found"));
            roles.add(defaultRole);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public boolean validateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void save(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Убираем принудительное добавление ROLE_USER
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalArgumentException("Default role ROLE_USER not found"));
            user.setRoles(Set.of(defaultRole));
        }

        userRepository.save(user);
    }

    public void saveWithRoles(User user, Set<Long> rolesId) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (rolesId != null && !rolesId.isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(rolesId));
            user.setRoles(roles);
        } else {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found"));
            user.setRoles(Set.of(defaultRole));
        }
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void updateUser(Long id, String username, String email,
                           String password, Set<Long> roleIds) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setUsername(username);
        user.setEmail(email);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        // Важная часть - обновление ролей
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> newRoles = new HashSet<>(roleRepository.findAllById(roleIds));
            user.setRoles(newRoles);
        } else {
            // Если роли не переданы, оставляем текущие
            Set<Role> currentRoles = user.getRoles();
            if (currentRoles == null || currentRoles.isEmpty()) {
                Role defaultRole = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new IllegalStateException("Default role not found"));
                user.setRoles(Set.of(defaultRole));
            }
        }

        userRepository.save(user);
    }


    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
}