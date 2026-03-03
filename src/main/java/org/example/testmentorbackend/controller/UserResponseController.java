package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.UserResponseDto;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.model.entity.UserResponses;
import org.example.testmentorbackend.services.OptionService;
import org.example.testmentorbackend.services.QuestionService;
import org.example.testmentorbackend.services.UserAttemptService;
import org.example.testmentorbackend.services.UserResponseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responses")
//@CrossOrigin("*")

public class UserResponseController {

    private final OptionService optionService;
    private final UserAttemptService userAttemptService;
    private final QuestionService questionService;
    private final UserResponseService userResponseService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserResponseController(OptionService optionService,
                                  UserAttemptService userAttemptService,
                                  QuestionService questionService,
                                  UserResponseService userResponseService,
                                  ModelMapper modelMapper) {
        this.optionService = optionService;
        this.userAttemptService = userAttemptService;
        this.questionService = questionService;
        this.userResponseService = userResponseService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<?> add(@RequestBody UserResponseDto dto) {
        try {
            UserResponses entity = modelMapper.map(dto, UserResponses.class);

            Questions q = questionService.findQuestionById(dto.getQuestionId());
            UserAttempt attempt = userAttemptService.findById(dto.getAttemptId());
            Options opt = optionService.findById(dto.getSelectedOptionID());

            entity.setQuestion(q);
            entity.setAttempt(attempt);
            entity.setSelectedOption(opt);

            return ResponseEntity.status(HttpStatus.CREATED).body(userResponseService.AddResponse(entity));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }
}
