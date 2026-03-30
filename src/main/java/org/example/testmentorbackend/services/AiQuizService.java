package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.AiAppendQuestionsRequestDto;
import org.example.testmentorbackend.dto.AiQuizGenerateRequestDto;
import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.dto.QuizzesDto;

import java.util.List;

public interface AiQuizService {
    QuizzesDto generateQuiz(AiQuizGenerateRequestDto request);

    List<QuestionDto> generateAdditionalQuestions(
            AiAppendQuestionsRequestDto request,
            String existingQuizTitle,
            String existingQuizDescription
    );
}