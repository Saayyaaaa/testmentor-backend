package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.OptionDto;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.services.OptionService;
import org.example.testmentorbackend.services.QuestionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/options")
@CrossOrigin("*")
public class OptionController {

    private final OptionService optionService;
    private final QuestionService questionService;
    private final ModelMapper modelMapper;

    @Autowired
    public OptionController(OptionService optionService, QuestionService questionService, ModelMapper modelMapper) {
        this.optionService = optionService;
        this.questionService = questionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Options> add(@RequestBody OptionDto optionDto) {
        Options option = modelMapper.map(optionDto, Options.class);
        Questions q = questionService.findQuestionById(optionDto.getQuestionId());
        option.setQuestions(q);
        Options saved = optionService.AddOptions(option);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
