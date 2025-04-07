package ru.itmentor.spring.boot_security.demo.repositories;

import org.springframework.stereotype.Repository;
import ru.itmentor.spring.boot_security.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndPasswordAndEmail(String nickname, String password, String email);
}
