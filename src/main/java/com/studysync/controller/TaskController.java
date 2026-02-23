package com.studysync.controller;

import com.studysync.dto.TaskRequest;
import com.studysync.model.Task;
import com.studysync.model.User;
import com.studysync.service.AuthService;
import com.studysync.service.TaskService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public TaskController(TaskService taskService, AuthService authService, JwtUtils jwtUtils) {
        this.taskService = taskService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(HttpServletRequest request, @RequestBody TaskRequest requestBody) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Task task = new Task();
        task.setTitle(requestBody.getTitle());
        task.setDescription(requestBody.getDescription());
        task.setTaskDate(requestBody.getTaskDate());
        task.setStartTime(requestBody.getStartTime());
        task.setEndTime(requestBody.getEndTime());
        task.setDurationHours(requestBody.getDurationHours());
        task.setTaskType(requestBody.getTaskType() != null ? requestBody.getTaskType() : Task.TaskType.STUDY);

        Task created = taskService.createTask(task, user.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Task> tasks = taskService.getTasksByUserId(user.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Task>> getTasksByDate(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Task> tasks = taskService.getTasksByDate(user.getId(), date);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Task> tasks = taskService.getIncompleteTasks(user.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody TaskRequest requestBody) {
        Task task = new Task();
        task.setTitle(requestBody.getTitle());
        task.setDescription(requestBody.getDescription());
        task.setTaskDate(requestBody.getTaskDate());
        task.setStartTime(requestBody.getStartTime());
        task.setEndTime(requestBody.getEndTime());
        task.setDurationHours(requestBody.getDurationHours());
        task.setTaskType(requestBody.getTaskType());
        task.setIsCompleted(requestBody.getDurationHours() != null ? null : null);

        Task updated = taskService.updateTask(id, task);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }

@PutMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskComplete(@PathVariable Long id) {
        Task task = new Task();
        task.setIsCompleted(true);
        
        Task updated = taskService.updateTask(id, task);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/pending")
    public ResponseEntity<Task> markTaskPending(@PathVariable Long id) {
        Task task = new Task();
        task.setIsCompleted(false);
        
        Task updated = taskService.updateTask(id, task);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @PostMapping("/reschedule")
    public ResponseEntity<List<Task>> rescheduleMissedTasks(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Task> tasks = taskService.rescheduleMissedTasks(user.getId());
        return ResponseEntity.ok(tasks);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
