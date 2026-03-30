package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizAttemptsOverviewDto {
    private Long quizId;
    private String quizTitle;

    // сколько уникальных людей проходило
    private long uniqueUsersCount;

    // сколько всего попыток было
    private long attemptsCount;

    // средний процент по всем попыткам
    private double averagePercent;

    // лучший процент по всем попыткам
    private double bestPercent;

    private List<QuizAttemptEntryDto> attempts;
}