package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.services.QuestionService;
import org.example.testmentorbackend.services.QuizzesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin("*")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizzesService quizzesService;
    private final ModelMapper modelMapper;

    @Autowired
    public QuestionController(QuestionService questionService, QuizzesService quizzesService, ModelMapper modelMapper) {
        this.questionService = questionService;
        this.quizzesService = quizzesService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Questions> add(@RequestBody QuestionDto questionDto) {
        Questions q = modelMapper.map(questionDto, Questions.class);
        Quizzes quiz = quizzesService.findById(questionDto.getQuizId());
        q.setQuizzes(quiz);
        Questions saved = questionService.AddQuestion(q);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
