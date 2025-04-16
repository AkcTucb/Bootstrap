package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.dto.UserDTO;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public AdminServiceImpl(UserDao userDao,
                            BCryptPasswordEncoder passwordEncoder,
                            RoleService roleService) {
        this.userDao         = userDao;
        this.passwordEncoder = passwordEncoder;
        this.roleService     = roleService;
    }


    @Override @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override @Transactional
    public void deleteUser(Long id) {
        userDao.deleteUser(id);
    }

    @Override @Transactional(readOnly = true)
    public User findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override @Transactional
    public void addUser(UserDTO dto) {
        User user = mapDtoToUser(dto);
        addUser(user);
    }

    @Override @Transactional
    public void update(UserDTO dto) {
        User existing = userDao.getUserById(dto.getId());
        if (existing == null) {
            throw new IllegalArgumentException(
                    "No user with id = " + dto.getId());
        }

        existing.setName(dto.getName());
        existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existing.setPassword(dto.getPassword());
        }
        existing.setRoles(extractRoles(dto));

        update(existing);
    }

    @Override @Transactional
    public void addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.saveUser(user);
    }

    @Override @Transactional
    public void update(User updatedUser) {
        User existing = userDao.getUserById(updatedUser.getId());
        if (existing == null) {
            throw new IllegalArgumentException(
                    "No user with id = " + updatedUser.getId());
        }

        existing.setName(updatedUser.getName());
        existing.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null
            && !updatedUser.getPassword().isEmpty()) {
            existing.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword()));
        }
        existing.setRoles(updatedUser.getRoles());
        userDao.updateUser(existing);
    }

    private User mapDtoToUser(UserDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRoles(extractRoles(dto));
        return user;
    }

    private Set<Role> extractRoles(UserDTO dto) {
        Set<Role> roles = new HashSet<>();
        if (dto.getRoleNames() != null) {
            for (String roleName : dto.getRoleNames()) {
                Role role = roleService.getRoleByName(roleName);
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }
}

