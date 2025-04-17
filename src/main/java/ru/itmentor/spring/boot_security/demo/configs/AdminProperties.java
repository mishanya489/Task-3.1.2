package ru.itmentor.spring.boot_security.demo.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private String username;
    private String password;
    private String email;
    private List<String> roles;
}
