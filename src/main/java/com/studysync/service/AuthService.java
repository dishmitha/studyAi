package com.studysync.service;

import com.studysync.dto.AuthResponse;
import com.studysync.dto.LoginRequest;
import com.studysync.dto.RegisterRequest;
import com.studysync.model.User;
import com.studysync.repository.UserRepository;
import com.studysync.util.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final HistoryService historyService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils, HistoryService historyService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.historyService = historyService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setDailyStudyHours(request.getDailyStudyHours() != null ? request.getDailyStudyHours() : 2);

        user = userRepository.save(user);

        // Add history for registration
        historyService.addAccountHistory(user, HistoryService.ACTION_REGISTERED, 
            "User registered with email: " + user.getEmail());

        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getName());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Add history for login
        historyService.addAccountHistory(user, HistoryService.ACTION_LOGIN, 
            "User logged in successfully");

        String token = jwtUtils.generateToken(user.getEmail(), user.getId(), user.getName());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public JwtUtils getJwtUtils() {
        return jwtUtils;
    }
}
