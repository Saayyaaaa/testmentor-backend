package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAttemptStatsDto {
    private long attempts;
    private int bestScore;
    private int lastScore;
    private double averageScore;
    private int totalQuestions;
    private int correctAnswers;
}
