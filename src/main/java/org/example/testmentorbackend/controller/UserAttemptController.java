package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.ErrorResponseDto;
import org.example.testmentorbackend.dto.UserAttemptDto;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.services.QuizzesService;
import org.example.testmentorbackend.services.UserAttemptService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/attempts")
@CrossOrigin("*")
public class UserAttemptController {

    private static final Logger logger = LoggerFactory.getLogger(UserAttemptController.class);

    private final UserAttemptService userAttemptService;
    private final QuizzesService quizzesService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserAttemptController(UserAttemptService userAttemptService, QuizzesService quizzesService, ModelMapper modelMapper) {
        this.userAttemptService = userAttemptService;
        this.quizzesService = quizzesService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<?> saveAttempt(@RequestBody UserAttemptDto dto) {
        try {
            UserAttempt attempt = modelMapper.map(dto, UserAttempt.class);
            Quizzes quiz = quizzesService.findById(dto.getQuizId());
            attempt.setQuizzes(quiz);
            return ResponseEntity.status(HttpStatus.CREATED).body(userAttemptService.addAttempt(attempt));
        } catch (Exception exception) {
            logger.error(exception.getMessage(), exception);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto(500, "Something went wrong"));
        }
    }
}
