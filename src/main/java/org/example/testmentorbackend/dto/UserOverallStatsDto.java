package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOverallStatsDto {
    private long attempts;
    private int bestScore;
    private int lastScore;
    private double averageScore;
    private long totalCorrectAnswers;
    private long totalQuestions;
    private long availableQuizzes;
}