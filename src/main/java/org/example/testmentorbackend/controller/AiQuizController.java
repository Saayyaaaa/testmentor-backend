package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.AiQuizGenerateRequestDto;
import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.services.AiQuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/quizzes")
public class AiQuizController {

    private final AiQuizService aiQuizService;

    public AiQuizController(AiQuizService aiQuizService) {
        this.aiQuizService = aiQuizService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<QuizzesDto> generate(@RequestBody AiQuizGenerateRequestDto request) {
        return ResponseEntity.ok(aiQuizService.generateQuiz(request));
    }
}