package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.services.QuizzesService;
import org.example.testmentorbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final QuizzesService quizzesService;

    @Autowired
    public AdminController(UserService userService, QuizzesService quizzesService) {
        this.userService = userService;
        this.quizzesService = quizzesService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@RequestParam String role) {
        return ResponseEntity.ok(userService.getByRole(role));
    }

    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> setRole(@PathVariable Long userId, @RequestParam String role) {
        return ResponseEntity.ok(userService.setRole(userId, role));
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizzesService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, Authentication authentication) {
        userService.deleteUser(userId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}