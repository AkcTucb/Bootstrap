package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.AdminService;
import ru.kata.spring.boot_security.demo.service.RoleService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final AdminService adminService;

    @Autowired
    public AdminController(RoleService roleService, AdminService adminService) {
        this.roleService  = roleService;
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
        adminService.addUser(userDto);
        return ResponseEntity.ok("Пользователь успешно создан");
    }

    @PostMapping(value = "/update", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDto) {
        adminService.update(userDto);
        return ResponseEntity.ok("Пользователь успешно обновлён");
    }

    @PostMapping(value = "/delete", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> deleteUser(@RequestBody UserDTO userDto) {
        if (userDto.getId() == null) {
            return ResponseEntity.badRequest()
                    .body("Не передан ID пользователя для удаления");
        }
        adminService.deleteUser(userDto.getId());
        return ResponseEntity.ok("Пользователь удалён");
    }
}

