package org.example.testmentorbackend.services;

import org.example.testmentorbackend.model.entity.Questions;

import java.util.List;

public interface QuestionService {
    Questions AddQuestion(Questions questions);
    List<Questions> getQuizzes();
    Questions findQuestionById(Long id);
}