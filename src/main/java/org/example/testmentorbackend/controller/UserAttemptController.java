package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.UserAttemptDto;
import org.example.testmentorbackend.dto.UserAttemptStatsDto;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.services.UserAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attempts")
//@CrossOrigin("*")

public class UserAttemptController {

    private final UserAttemptService userAttemptService;

    public UserAttemptController(UserAttemptService userAttemptService) {
        this.userAttemptService = userAttemptService;
    }

    // Save attempt (used by submit endpoint too)
    @PostMapping
    public ResponseEntity<UserAttempt> saveAttempt(@RequestBody UserAttemptDto dto, Authentication authentication) {
        String username = authentication.getName();
        UserAttempt saved = userAttemptService.saveAttempt(username, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // Get stats for current user + quiz
    @GetMapping("/stats")
    public ResponseEntity<UserAttemptStatsDto> stats(@RequestParam Long quizId, Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userAttemptService.getStats(username, quizId));
    }
}
