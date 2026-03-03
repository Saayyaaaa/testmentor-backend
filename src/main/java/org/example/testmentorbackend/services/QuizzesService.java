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

    // 🔹 НОВЫЙ метод — нужен для страницы деталей квиза
    Quizzes findDetailsById(Long id);

    // 🔹 НОВЫЙ метод — для ReviewPanel с myVote
    List<MentorReviewQuizDto> getPendingQuizzesForMentorsWithMyVote(String mentorUsername);

    void deleteQuiz(Long id);
}