package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.AiQuizGenerateRequestDto;
import org.example.testmentorbackend.dto.QuizzesDto;

public interface AiQuizService {
    QuizzesDto generateQuiz(AiQuizGenerateRequestDto request);
}