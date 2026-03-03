package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.services.QuestionService;
import org.example.testmentorbackend.services.QuizzesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints for creating questions (used by Postman/manual seeding).
 */
@RestController
@RequestMapping("/api/questions")
@CrossOrigin("*")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizzesService quizzesService;

    public QuestionController(QuestionService questionService, QuizzesService quizzesService) {
        this.questionService = questionService;
        this.quizzesService = quizzesService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MENTOR')")
    public ResponseEntity<Questions> add(@RequestBody QuestionDto dto) {
        if (dto == null || dto.getQuizId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Quizzes quiz = quizzesService.findById(dto.getQuizId());

        Questions q = new Questions();
        q.setQuestionText(dto.getQuestionText());
        q.setQuestionType(dto.getQuestionType());
        q.setAiAnswer(dto.getAiAnswer());
        q.setQuizzes(quiz);

        Questions saved = questionService.AddQuestion(q);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
