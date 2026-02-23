package com.studysync.controller;

import com.studysync.dto.StudyPlanRequest;
import com.studysync.model.StudyPlan;
import com.studysync.model.User;
import com.studysync.service.AuthService;
import com.studysync.service.StudyPlanService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/study-plans")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public StudyPlanController(StudyPlanService studyPlanService, AuthService authService, JwtUtils jwtUtils) {
        this.studyPlanService = studyPlanService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<StudyPlan> createStudyPlan(HttpServletRequest request, @RequestBody StudyPlanRequest requestBody) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setPlanName(requestBody.getPlanName());
        studyPlan.setStartDate(requestBody.getStartDate());
        studyPlan.setEndDate(requestBody.getEndDate());

        StudyPlan created = studyPlanService.createStudyPlan(studyPlan, user.getId());
        return ResponseEntity.ok(created);
    }

    @PostMapping("/generate")
    public ResponseEntity<StudyPlan> generateAutoTimetable(
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

        StudyPlan studyPlan = studyPlanService.generateAutoTimetable(user.getId(), startDate, endDate);
        return ResponseEntity.ok(studyPlan);
    }

    @GetMapping
    public ResponseEntity<List<StudyPlan>> getStudyPlans(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        List<StudyPlan> plans = studyPlanService.getStudyPlansByUserId(user.getId());
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/active")
    public ResponseEntity<StudyPlan> getActiveStudyPlan(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        String email = jwtUtils.extractEmailFromToken(token);
        User user = authService.getUserByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        return studyPlanService.getActiveStudyPlan(user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudyPlan> getStudyPlanById(@PathVariable Long id) {
        return studyPlanService.getStudyPlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudyPlan> updateStudyPlan(@PathVariable Long id, @RequestBody StudyPlanRequest requestBody) {
        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setPlanName(requestBody.getPlanName());
        studyPlan.setStartDate(requestBody.getStartDate());
        studyPlan.setEndDate(requestBody.getEndDate());

        StudyPlan updated = studyPlanService.updateStudyPlan(id, studyPlan);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudyPlan(@PathVariable Long id) {
        studyPlanService.deleteStudyPlan(id);
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
