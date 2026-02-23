package com.studysync.controller;

import com.studysync.dto.UserUpdateRequest;
import com.studysync.model.User;
import com.studysync.service.UserService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(HttpServletRequest request, @RequestBody UserUpdateRequest requestBody) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        return userService.getUserByEmail(email)
                .map(user -> {
                    if (requestBody.getName() != null) user.setName(requestBody.getName());
                    if (requestBody.getPhone() != null) user.setPhone(requestBody.getPhone());
                    if (requestBody.getDateOfBirth() != null) user.setDateOfBirth(requestBody.getDateOfBirth());
                    if (requestBody.getInstitution() != null) user.setInstitution(requestBody.getInstitution());
                    if (requestBody.getDailyStudyHours() != null) user.setDailyStudyHours(requestBody.getDailyStudyHours());
                    return ResponseEntity.ok(userService.updateUser(user.getId(), user));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
