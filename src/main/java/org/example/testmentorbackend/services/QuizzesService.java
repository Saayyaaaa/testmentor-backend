package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.MentorReviewQuizDto;
import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.model.entity.Quizzes;

import java.util.List;

public interface QuizzesService {

    Quizzes createQuiz(QuizzesDto dto, String authorUsername);

    List<Quizzes> getAllQuizzesForAdmin();

    List<Quizzes> getApprovedQuizzesForStudents();

    List<Quizzes> getPendingQuizzesForMentors();

    Quizzes findById(Long id);

    Quizzes findDetailsById(Long id);

    List<MentorReviewQuizDto> getPendingQuizzesForMentorsWithMyVote(String mentorUsername);

    void deleteQuiz(Long id);

    List<MentorReviewQuizDto> getReviewQuizzes(String username, boolean onlyMine, boolean isAdmin);

    Quizzes updateQuizMeta(Long quizId, String username, boolean isAdmin, QuizzesDto dto);

    void deleteQuizForReviewPanel(Long quizId, String username, boolean isAdmin);
}