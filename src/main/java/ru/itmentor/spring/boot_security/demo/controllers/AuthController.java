package ru.itmentor.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.dto.UserDto;
import ru.itmentor.spring.boot_security.demo.dto.UserRegistrationDto;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.util.Set;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute("userDto") UserDto userDto) {
        if (userService.validateUser(userDto.getUsername(), userDto.getPassword())) {
            return "redirect:/dashboard";
        }
        return "redirect:/auth/login?error";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserRegistrationDto userDto,
                           @RequestParam("roleIds") Set<Long> roleIds,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "auth/register";
        }
        userService.registerNewUser(userDto, roleIds);
        return "redirect:/auth/login";
    }
}
