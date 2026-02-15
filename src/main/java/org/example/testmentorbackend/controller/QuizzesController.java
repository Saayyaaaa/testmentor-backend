package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.services.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin("*")
public class QuizzesController {

    private final QuizzesService quizzesService;

    @Autowired
    public QuizzesController(QuizzesService quizzesService) {
        this.quizzesService = quizzesService;
    }

    @PostMapping
    public ResponseEntity<Quizzes> create(@RequestBody QuizzesDto dto, Authentication authentication) {
        String username = authentication.getName();
        Quizzes created = quizzesService.createQuiz(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Quizzes>> list(Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isMentor = hasRole(authentication, "ROLE_MENTOR");

        if (isAdmin) {
            return ResponseEntity.ok(quizzesService.getAllQuizzesForAdmin());
        }
        if (isMentor) {
            return ResponseEntity.ok(quizzesService.getApprovedQuizzesForStudents());
        }
        return ResponseEntity.ok(quizzesService.getApprovedQuizzesForStudents());
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
