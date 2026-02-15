package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.services.QuizzesService;
import org.example.testmentorbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    private final UserService userService;
    private final QuizzesService quizzesService;

    @Autowired
    public AdminController(UserService userService, QuizzesService quizzesService) {
        this.userService = userService;
        this.quizzesService = quizzesService;
    }

    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> setRole(@PathVariable Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.setRole(userId, role));
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizzesService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}
