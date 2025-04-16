package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface AdminService {

    List<User> getAllUsers();

    void addUser(UserDTO dto);
    void update(UserDTO dto);

    void addUser(User user);
    void update(User user);

    void deleteUser(Long id);
    User findById(Long id);
}
