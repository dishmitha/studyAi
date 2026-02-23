package com.studysync.controller;

import com.studysync.model.Progress;
import com.studysync.model.User;
import com.studysync.service.AuthService;
import com.studysync.service.ProgressService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public ProgressController(ProgressService progressService, AuthService authService, JwtUtils jwtUtils) {
        this.progressService = progressService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<List<Progress>> getProgress(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Progress> progressList = progressService.getProgressByUserId(user.getId());
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/date")
    public ResponseEntity<Progress> getProgressForDate(
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

        return progressService.getProgressForDate(user.getId(), date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/range")
    public ResponseEntity<List<Progress>> getProgressBetweenDates(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Progress> progressList = progressService.getProgressBetweenDates(user.getId(), startDate, endDate);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/streak")
    public ResponseEntity<Map<String, Object>> getStudyStreak(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        int streak = progressService.calculateStudyStreak(user.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("streak", streak);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overall")
    public ResponseEntity<Progress> getOverallProgress(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Progress progress = progressService.getOverallProgress(user.getId());
        if (progress != null) {
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Double totalHours = progressService.getTotalStudyHours(user.getId());
        Integer activeDays = progressService.getActiveStudyDays(user.getId());
        int streak = progressService.calculateStudyStreak(user.getId());
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudyHours", totalHours != null ? totalHours : 0.0);
        stats.put("activeDays", activeDays != null ? activeDays : 0);
        stats.put("currentStreak", streak);
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/update")
    public ResponseEntity<Progress> updateDailyProgress(
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

        Progress progress = progressService.createOrUpdateDailyProgress(user.getId(), date);
        return ResponseEntity.ok(progress);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
