package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.dto.QuizAttemptEntryDto;
import org.example.testmentorbackend.dto.QuizAttemptsOverviewDto;
import org.example.testmentorbackend.dto.UserAttemptDto;
import org.example.testmentorbackend.dto.UserAttemptStatsDto;
import org.example.testmentorbackend.dto.UserOverallStatsDto;
import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.example.testmentorbackend.repository.QuizzesRepository;
import org.example.testmentorbackend.repository.UserAttemptRepository;
import org.example.testmentorbackend.repository.UserRepository;
import org.example.testmentorbackend.services.UserAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserAttemptServiceImpl implements UserAttemptService {

    private final UserAttemptRepository userAttemptRepository;
    private final UserRepository userRepository;
    private final QuizzesRepository quizzesRepository;

    @Autowired
    public UserAttemptServiceImpl(
            UserAttemptRepository userAttemptRepository,
            UserRepository userRepository,
            QuizzesRepository quizzesRepository
    ) {
        this.userAttemptRepository = userAttemptRepository;
        this.userRepository = userRepository;
        this.quizzesRepository = quizzesRepository;
    }

    @Override
    public UserAttempt saveAttempt(String username, UserAttemptDto dto) {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Quizzes quiz = quizzesRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + dto.getQuizId()));

        UserAttempt attempt = new UserAttempt();
        attempt.setUser(user);
        attempt.setQuizzes(quiz);

        attempt.setScore(dto.getScore());
        attempt.setTotalQuestions(dto.getTotalQuestions());
        attempt.setCorrectAnswers(dto.getCorrectAnswers());

        attempt.setStartTime(dto.getStartTime() != null ? dto.getStartTime() : LocalDateTime.now());
        attempt.setEndTime(dto.getEndTime() != null ? dto.getEndTime() : LocalDateTime.now());

        return userAttemptRepository.save(attempt);
    }

    @Override
    public UserAttempt findById(Long id) {
        return userAttemptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attempt not found: " + id));
    }

    @Override
    public List<UserAttempt> findByUser(String username) {
        return userAttemptRepository.findByUser_NameOrderByEndTimeDesc(username);
    }

    @Override
    public List<UserAttempt> findByQuiz(Long quizId) {
        return userAttemptRepository.findByQuizzes_QuizIDOrderByEndTimeDesc(quizId);
    }

    @Override
    public UserAttemptStatsDto getStats(String username, Long quizId) {
        long attempts = userAttemptRepository.countByUserAndQuiz(username, quizId);
        int best = safeInt(userAttemptRepository.bestScore(username, quizId));
        double avg = safeDouble(userAttemptRepository.avgScore(username, quizId));

        List<UserAttempt> lastList = userAttemptRepository
                .findByUser_NameAndQuizzes_QuizIDOrderByEndTimeDesc(username, quizId);

        int lastScore = lastList.isEmpty() ? 0 : lastList.get(0).getScore();
        int totalQuestions = lastList.isEmpty() ? 0 : lastList.get(0).getTotalQuestions();
        int correctAnswers = lastList.isEmpty() ? 0 : lastList.get(0).getCorrectAnswers();

        return new UserAttemptStatsDto(attempts, best, lastScore, avg, totalQuestions, correctAnswers);
    }

    @Override
    public UserOverallStatsDto getOverallStats(String username) {
        long attempts = userAttemptRepository.countByUser_Name(username);
        int best = safeInt(userAttemptRepository.bestScoreOverall(username));
        double avg = safeDouble(userAttemptRepository.avgScoreOverall(username));

        List<UserAttempt> attemptsList = userAttemptRepository.findByUser_NameOrderByEndTimeDesc(username);
        int lastScore = attemptsList.isEmpty() ? 0 : attemptsList.get(0).getScore();

        long totalCorrect = safeLong(userAttemptRepository.totalCorrectAnswersOverall(username));
        long totalQuestions = safeLong(userAttemptRepository.totalQuestionsOverall(username));
        long availableQuizzes = quizzesRepository.countByStatus(TestStatus.APPROVED);

        return new UserOverallStatsDto(
                attempts,
                best,
                lastScore,
                avg,
                totalCorrect,
                totalQuestions,
                availableQuizzes
        );
    }

    @Override
    public QuizAttemptsOverviewDto getQuizAttemptsOverview(Long quizId, String requesterUsername, boolean isAdmin) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        if (!isAdmin) {
            if (quiz.getAuthor() == null || quiz.getAuthor().getName() == null ||
                    !quiz.getAuthor().getName().equals(requesterUsername)) {
                throw new AccessDeniedException("You can view statistics only for your own quizzes");
            }
        }

        List<UserAttempt> attempts = userAttemptRepository.findByQuizzes_QuizIDOrderByEndTimeDesc(quizId);
        long uniqueUsersCount = userAttemptRepository.countDistinctUsersByQuiz(quizId);

        double totalPercent = 0.0;
        double bestPercent = 0.0;

        List<QuizAttemptEntryDto> entries = attempts.stream()
                .map(a -> {
                    double percent = calculatePercent(a.getCorrectAnswers(), a.getTotalQuestions());
                    return new QuizAttemptEntryDto(
                            a.getAttemptID(),
                            a.getUser() != null ? a.getUser().getName() : "Unknown",
                            a.getScore(),
                            a.getCorrectAnswers(),
                            a.getTotalQuestions(),
                            percent,
                            a.getEndTime()
                    );
                })
                .toList();

        for (QuizAttemptEntryDto entry : entries) {
            totalPercent += entry.getPercent();
            if (entry.getPercent() > bestPercent) {
                bestPercent = entry.getPercent();
            }
        }

        double averagePercent = entries.isEmpty() ? 0.0 : totalPercent / entries.size();

        return new QuizAttemptsOverviewDto(
                quiz.getQuizID(),
                quiz.getTitle(),
                uniqueUsersCount,
                entries.size(),
                round2(averagePercent),
                round2(bestPercent),
                entries
        );
    }

    private double calculatePercent(int correctAnswers, int totalQuestions) {
        if (totalQuestions <= 0) return 0.0;
        return (correctAnswers * 100.0) / totalQuestions;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private int safeInt(Integer v) {
        return v == null ? 0 : v;
    }

    private double safeDouble(Double v) {
        return v == null ? 0.0 : v;
    }

    private long safeLong(Long v) {
        return v == null ? 0L : v;
    }
}