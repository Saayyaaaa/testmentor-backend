package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.UserAttemptDto;
import org.example.testmentorbackend.dto.UserAttemptStatsDto;
import org.example.testmentorbackend.dto.UserOverallStatsDto;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.dto.QuizAttemptsOverviewDto;
import java.util.List;

public interface UserAttemptService {

    UserAttempt saveAttempt(String username, UserAttemptDto dto);

    UserAttempt findById(Long id);

    List<UserAttempt> findByUser(String username);

    List<UserAttempt> findByQuiz(Long quizId);

    UserAttemptStatsDto getStats(String username, Long quizId);

    UserOverallStatsDto getOverallStats(String username);

    QuizAttemptsOverviewDto getQuizAttemptsOverview(Long quizId, String requesterUsername, boolean isAdmin);

}