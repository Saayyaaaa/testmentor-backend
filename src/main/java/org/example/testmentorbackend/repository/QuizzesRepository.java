package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizzesRepository extends JpaRepository<Quizzes, Long> {
    List<Quizzes> findAllByStatus(TestStatus status);
}
