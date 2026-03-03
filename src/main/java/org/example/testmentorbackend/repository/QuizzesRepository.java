package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizzesRepository extends JpaRepository<Quizzes, Long> {

    List<Quizzes> findAllByStatus(TestStatus status);

    @Query("""
        select distinct q
        from Quizzes q
        left join fetch q.questions qu
        where q.quizID = :id
    """)
    Optional<Quizzes> findDetailsById(@Param("id") Long id);
}