package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers();

    /*  Новые сигнатуры, принимающие DTO  */
    void addUser(UserDTO dto);
    void update(UserDTO dto);

    /*  Прежние методы можно оставить,
        если их использует остальной код  */
    void addUser(User user);
    void update(User user);

    void deleteUser(Long id);
    User findById(Long id);
}
