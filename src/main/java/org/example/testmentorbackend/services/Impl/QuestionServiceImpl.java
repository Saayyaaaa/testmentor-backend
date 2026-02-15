package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.repository.QuestionRepository;
import org.example.testmentorbackend.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public Questions AddQuestion(Questions questions) {
        return this.questionRepository.save(questions);
    }

    @Override
    public List<Questions> getQuizzes() {
        return this.questionRepository.findAll();
    }

    @Override
    public Questions findQuestionById(Long id) {
        return this.questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found: " + id));
    }
}
