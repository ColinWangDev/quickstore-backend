package com.quickstore.service;

import com.quickstore.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    User save(User user);
    List<User> findAllUsers();
    User findById(Long id);
    void deleteUser(Long id);
} 