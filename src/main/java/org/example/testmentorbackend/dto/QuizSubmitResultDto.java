package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmitResultDto {
    private Long attemptId;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
}
