package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptEntryDto {
    private Long attemptId;
    private String username;
    private int score;
    private int correctAnswers;
    private int totalQuestions;
    private double percent;
    private LocalDateTime endTime;
}