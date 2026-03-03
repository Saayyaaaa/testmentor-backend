package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAttemptRepository extends JpaRepository<UserAttempt, Long> {

    // all attempts for quiz
    List<UserAttempt> findByQuizzes_QuizID(Long quizId);

    // all attempts by user
    List<UserAttempt> findByUser_NameOrderByEndTimeDesc(String username);

    // attempts by user for a quiz (for last attempt)
    List<UserAttempt> findByUser_NameAndQuizzes_QuizIDOrderByEndTimeDesc(String username, Long quizId);

    @Query("select count(a) from UserAttempt a where a.user.name = :username and a.quizzes.quizID = :quizId")
    long countByUserAndQuiz(@Param("username") String username, @Param("quizId") Long quizId);

    @Query("select max(a.score) from UserAttempt a where a.user.name = :username and a.quizzes.quizID = :quizId")
    Integer bestScore(@Param("username") String username, @Param("quizId") Long quizId);

    @Query("select avg(a.score) from UserAttempt a where a.user.name = :username and a.quizzes.quizID = :quizId")
    Double avgScore(@Param("username") String username, @Param("quizId") Long quizId);
}
