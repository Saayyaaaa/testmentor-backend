package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.OptionDto;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.services.OptionService;
import org.example.testmentorbackend.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/options")
//@CrossOrigin("*")
public class OptionController {

    private final OptionService optionService;
    private final QuestionService questionService;

    @Autowired
    public OptionController(OptionService optionService, QuestionService questionService) {
        this.optionService = optionService;
        this.questionService = questionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','MENTOR')")
    public ResponseEntity<Options> add(@RequestBody OptionDto optionDto) {
        if (optionDto.getOptionText() == null || optionDto.getOptionText().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Questions q = questionService.findQuestionById(optionDto.getQuestionId());

        Options option = new Options();
        option.setOptionText(optionDto.getOptionText());
        option.setCorrect(optionDto.isCorrect());
        option.setQuestions(q);

        Options saved = optionService.AddOptions(option);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
