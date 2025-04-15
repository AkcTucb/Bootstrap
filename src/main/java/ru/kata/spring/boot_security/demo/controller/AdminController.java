package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.AdminService;
import ru.kata.spring.boot_security.demo.service.RoleService;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final AdminService adminService;

    @Autowired
    public AdminController(RoleService roleService, AdminService adminService) {
        this.roleService = roleService;
        this.adminService = adminService;
    }


    @GetMapping
    public String showAdminPage(Model model,
                                @AuthenticationPrincipal User authUser) {
        model.addAttribute("currentUser", authUser);
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());

        return "adminPage";
    }


    @PostMapping(value = "/userForm", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> saveUser(@RequestBody UserDTO userDto) {

        User newUser = new User();
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(userDto.getPassword());


        Set<Role> roles = new HashSet<>();
        if (userDto.getRoleNames() != null) {
            for (String roleName : userDto.getRoleNames()) {
                Role role = roleService.getRoleByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        newUser.setRoles(roles);

        adminService.addUser(newUser);
        return ResponseEntity.ok("Пользователь успешно создан");
    }


    @PostMapping(value = "/update", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDto) {

        User existingUser = adminService.findById(userDto.getId());
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("Пользователь с таким ID не найден");
        }


        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            existingUser.setPassword(userDto.getPassword());
        }


        Set<Role> roles = new HashSet<>();
        if (userDto.getRoleNames() != null) {
            for (String roleName : userDto.getRoleNames()) {
                Role role = roleService.getRoleByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        existingUser.setRoles(roles);


        adminService.update(existingUser);
        return ResponseEntity.ok("Пользователь успешно обновлён");
    }

    @PostMapping(value = "/delete", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO userDto) {
        if (userDto.getId() == null) {
            return ResponseEntity.badRequest().body("Не передан ID пользователя для удаления");
        }
        adminService.deleteUser(userDto.getId());
        return ResponseEntity.ok("Пользователь удалён");
    }
}
