package com.studysync.controller;

import com.studysync.model.History;
import com.studysync.model.User;
import com.studysync.service.AuthService;
import com.studysync.service.HistoryService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public HistoryController(HistoryService historyService, AuthService authService, JwtUtils jwtUtils) {
        this.historyService = historyService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserHistory(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            Long userId = jwtUtils.extractUserIdFromToken(token);
            User user = authService.getUserById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            if (page > 0) {
                Page<History> historyPage = historyService.getUserHistoryPaged(user.getId(), page - 1, size);
                Map<String, Object> response = new HashMap<>();
                response.put("content", historyPage.getContent());
                response.put("totalPages", historyPage.getTotalPages());
                response.put("totalElements", historyPage.getTotalElements());
                response.put("currentPage", page);
                return ResponseEntity.ok(response);
            } else {
                List<History> history = historyService.getUserHistory(user.getId());
                Map<String, Object> response = new HashMap<>();
                response.put("content", history);
                response.put("totalElements", history.size());
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getHistoryByType(
            HttpServletRequest request,
            @PathVariable String type) {
        
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            Long userId = jwtUtils.extractUserIdFromToken(token);
            User user = authService.getUserById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<History> history = historyService.getUserHistoryByType(user.getId(), type);
            Map<String, Object> response = new HashMap<>();
            response.put("content", history);
            response.put("totalElements", history.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getHistorySummary(HttpServletRequest request) {
        
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            Long userId = jwtUtils.extractUserIdFromToken(token);
            User user = authService.getUserById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            List<History> allHistory = historyService.getUserHistory(user.getId());
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalEntries", allHistory.size());
            
            long accountCount = allHistory.stream().filter(h -> "ACCOUNT".equals(h.getType())).count();
            long subjectCount = allHistory.stream().filter(h -> "SUBJECT".equals(h.getType())).count();
            long taskCount = allHistory.stream().filter(h -> "TASK".equals(h.getType())).count();
            long studySessionCount = allHistory.stream().filter(h -> "STUDY_SESSION".equals(h.getType())).count();
            
            summary.put("accountActivities", accountCount);
            summary.put("subjectActivities", subjectCount);
            summary.put("taskActivities", taskCount);
            summary.put("studySessions", studySessionCount);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createHistoryEntry(
            HttpServletRequest request,
            @RequestBody Map<String, String> requestBody) {
        
        try {
            String token = extractTokenFromRequest(request);
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            Long userId = jwtUtils.extractUserIdFromToken(token);
            User user = authService.getUserById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            
            String type = requestBody.get("type");
            String action = requestBody.get("action");
            String description = requestBody.get("description");
            String entityName = requestBody.get("entityName");
            String metadata = requestBody.get("metadata");
            
            History history = new History();
            history.setUser(user);
            history.setType(type);
            history.setAction(action);
            history.setDescription(description);
            history.setEntityName(entityName);
            history.setMetadata(metadata);
            
            History saved = historyService.createHistory(history);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", saved.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
