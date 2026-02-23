package com.studysync.controller;

import com.studysync.dto.ReminderRequest;
import com.studysync.model.Reminder;
import com.studysync.model.User;
import com.studysync.service.AuthService;
import com.studysync.service.ReminderService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public ReminderController(ReminderService reminderService, AuthService authService, JwtUtils jwtUtils) {
        this.reminderService = reminderService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<Reminder> createReminder(HttpServletRequest request, @RequestBody ReminderRequest requestBody) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Reminder reminder = new Reminder();
        reminder.setTitle(requestBody.getTitle());
        reminder.setMessage(requestBody.getMessage());
        reminder.setReminderTime(requestBody.getReminderTime());
        reminder.setReminderType(requestBody.getReminderType() != null ? requestBody.getReminderType() : Reminder.ReminderType.CUSTOM);
        reminder.setIsActive(true);

        Reminder created = reminderService.createReminder(reminder, user.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Reminder>> getReminders(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Reminder> reminders = reminderService.getRemindersByUserId(user.getId());
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Reminder>> getActiveReminders(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Reminder> reminders = reminderService.getActiveReminders(user.getId());
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getReminderById(@PathVariable Long id) {
        return reminderService.getReminderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(@PathVariable Long id, @RequestBody ReminderRequest requestBody) {
        Reminder reminder = new Reminder();
        reminder.setTitle(requestBody.getTitle());
        reminder.setMessage(requestBody.getMessage());
        reminder.setReminderTime(requestBody.getReminderTime());
        reminder.setReminderType(requestBody.getReminderType());
        if (requestBody.getIsActive() != null) {
            reminder.setIsActive(requestBody.getIsActive());
        }

        Reminder updated = reminderService.updateReminder(id, reminder);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/daily")
    public ResponseEntity<List<Reminder>> createDailyReminder(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Reminder> reminders = reminderService.createDailyReminders(user.getId());
        return ResponseEntity.ok(reminders);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
