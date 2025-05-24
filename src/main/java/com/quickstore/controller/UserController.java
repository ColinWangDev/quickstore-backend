package com.quickstore.controller;

import com.quickstore.dto.ChangePasswordRequest;
import com.quickstore.dto.ResetPasswordRequest;
import com.quickstore.dto.UserUpdateRequest;
import com.quickstore.model.User;
import com.quickstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Attempting to get all users");
        List<User> users = userService.findAllUsers();
        logger.info("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        logger.info("Attempting to update user with id: {}", id);
        
        User user = userService.findById(id);
        if (user == null) {
            logger.warn("User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        // 更新用户信息
        user.setFullName(request.getFullName());
        user.setRole(request.getRole().toLowerCase());

        // 保存更新
        userService.save(user);
        logger.info("User updated successfully: {}", user.getUsername());

        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Attempting to delete user with id: {}", id);
        
        User user = userService.findById(id);
        if (user == null) {
            logger.warn("User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }

        userService.deleteUser(id);
        logger.info("User deleted successfully: {}", user.getUsername());
        
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody ChangePasswordRequest request) {
        String username = authentication.getName();
        logger.info("Attempting to change password for user: {}", username);
        
        try {
            userService.changePassword(username, request.getOldPassword(), request.getNewPassword());
            logger.info("Password changed successfully for user: {}", username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to change password for user: {}", username, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody ResetPasswordRequest request) {
        logger.info("Attempting to reset password for user id: {}", id);
        
        try {
            userService.resetPassword(id, request.getNewPassword());
            logger.info("Password reset successfully for user id: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to reset password for user id: {}", id, e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 