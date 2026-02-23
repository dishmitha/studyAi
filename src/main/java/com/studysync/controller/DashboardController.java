package com.studysync.controller;

import com.studysync.dto.DashboardResponse;
import com.studysync.service.DashboardService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final JwtUtils jwtUtils;

    public DashboardController(DashboardService dashboardService, JwtUtils jwtUtils) {
        this.dashboardService = dashboardService;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = jwtUtils.extractUserIdFromToken(token);
        DashboardResponse dashboard = dashboardService.getDashboard(userId);
        return ResponseEntity.ok(dashboard);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
