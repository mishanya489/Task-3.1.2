package ru.itmentor.spring.boot_security.demo.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserRegistrationDto {
    @NotBlank(message = "Имя обязательно")
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
    private String password;

    @Email
    private String email;
}
