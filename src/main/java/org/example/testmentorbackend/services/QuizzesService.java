package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.model.entity.Quizzes;

import java.util.List;

public interface QuizzesService {

    Quizzes createQuiz(QuizzesDto dto, String authorUsername);

    List<Quizzes> getAllQuizzesForAdmin();

    List<Quizzes> getApprovedQuizzesForStudents();

    List<Quizzes> getPendingQuizzesForMentors();

    Quizzes findById(Long id);

    void deleteQuiz(Long id);
}
