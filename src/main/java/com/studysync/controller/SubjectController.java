package com.studysync.controller;

import com.studysync.dto.SubjectRequest;
import com.studysync.model.Subject;
import com.studysync.model.User;
import com.studysync.service.AuthService;
import com.studysync.service.SubjectService;
import com.studysync.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;
    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public SubjectController(SubjectService subjectService, AuthService authService, JwtUtils jwtUtils) {
        this.subjectService = subjectService;
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping
    public ResponseEntity<Subject> createSubject(HttpServletRequest request, @RequestBody SubjectRequest requestBody) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = jwtUtils.extractUserIdFromToken(token);
        User user = authService.getUserById(userId).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        Subject subject = new Subject();
        subject.setName(requestBody.getName());
        subject.setDescription(requestBody.getDescription());
        subject.setExamDate(requestBody.getExamDate());
        subject.setPriority(requestBody.getPriority() != null ? requestBody.getPriority() : Subject.Priority.MEDIUM);

        Subject created = subjectService.createSubject(subject, user.getId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getSubjects(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null || !jwtUtils.validateToken(token)) {
            return ResponseEntity.status(401).build();
        }
        
        Long userId = jwtUtils.extractUserIdFromToken(token);
        List<Subject> subjects = subjectService.getSubjectsByUserId(userId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody SubjectRequest request) {
        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setDescription(request.getDescription());
        subject.setExamDate(request.getExamDate());
        subject.setPriority(request.getPriority());

        Subject updated = subjectService.updateSubject(id, subject);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
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
