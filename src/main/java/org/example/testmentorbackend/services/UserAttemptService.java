package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.UserAttemptDto;
import org.example.testmentorbackend.dto.UserAttemptStatsDto;
import org.example.testmentorbackend.model.entity.UserAttempt;

import java.util.List;

public interface UserAttemptService {

    UserAttempt saveAttempt(String username, UserAttemptDto dto);

    UserAttempt findById(Long id);

    List<UserAttempt> findByUser(String username);

    List<UserAttempt> findByQuiz(Long quizId);

    UserAttemptStatsDto getStats(String username, Long quizId);
}
