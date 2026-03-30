package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByQuiz_QuizIDAndMentor_Id(Long quizId, Long mentorId);

    @Query("""
        select v
        from Vote v
        left join fetch v.mentor m
        where v.quiz.quizID = :quizId
        order by v.createdAt desc
    """)
    List<Vote> findAllDetailedByQuizId(@Param("quizId") Long quizId);

    List<Vote> findAllByQuiz_QuizIDOrderByCreatedAtDesc(Long quizId);

    long countByQuiz_QuizID(Long quizId);
}