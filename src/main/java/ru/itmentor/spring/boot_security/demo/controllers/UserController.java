package ru.itmentor.spring.boot_security.demo.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.models.User;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String listUsers(Model model, Authentication authentication) {
        model.addAttribute("users", userService.findAll());
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "users/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model){
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/form";
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(@ModelAttribute @Valid User user,
                             BindingResult bindingResult,
                             @RequestParam(required = false) Set<Long> roles,
                             Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "users/form";
        }
        userService.saveWithRoles(user, roles);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "users/edit";
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String username,
                             @RequestParam String email,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) Set<Long> roleIds) {

        System.out.println("Received role IDs: " + roleIds); // Для отладки

        userService.updateUser(id, username, email, password, roleIds);
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}