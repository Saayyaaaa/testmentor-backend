package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByQuiz_QuizIDAndMentor_Id(Long quizId, Long mentorId);
    long countByQuiz_QuizID(Long quizId);
}
