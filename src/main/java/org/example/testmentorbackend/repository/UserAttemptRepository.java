package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserAttemptRepository extends JpaRepository<UserAttempt, Long> {

    List<UserAttempt> findByQuizzes_QuizIDOrderByEndTimeDesc(Long quizId);

    @Query("select count(distinct a.user.id) from UserAttempt a where a.quizzes.quizID = :quizId")
    long countDistinctUsersByQuiz(@Param("quizId") Long quizId);

    List<UserAttempt> findByUser_NameOrderByEndTimeDesc(String username);

    List<UserAttempt> findByUser_NameAndQuizzes_QuizIDOrderByEndTimeDesc(String username, Long quizId);

    @Query("select count(a) from UserAttempt a where a.user.name = :username and a.quizzes.quizID = :quizId")
    long countByUserAndQuiz(@Param("username") String username, @Param("quizId") Long quizId);

    @Query("select max(a.score) from UserAttempt a where a.user.name = :username and a.quizzes.quizID = :quizId")
    Integer bestScore(@Param("username") String username, @Param("quizId") Long quizId);

    @Query("select avg(a.score) from UserAttempt a where a.user.name = :username and a.quizzes.quizID = :quizId")
    Double avgScore(@Param("username") String username, @Param("quizId") Long quizId);

    long countByUser_Name(String username);

    @Query("select max(a.score) from UserAttempt a where a.user.name = :username")
    Integer bestScoreOverall(@Param("username") String username);

    @Query("select avg(a.score) from UserAttempt a where a.user.name = :username")
    Double avgScoreOverall(@Param("username") String username);

    @Query("select coalesce(sum(a.correctAnswers), 0) from UserAttempt a where a.user.name = :username")
    Long totalCorrectAnswersOverall(@Param("username") String username);

    @Query("select coalesce(sum(a.totalQuestions), 0) from UserAttempt a where a.user.name = :username")
    Long totalQuestionsOverall(@Param("username") String username);
}