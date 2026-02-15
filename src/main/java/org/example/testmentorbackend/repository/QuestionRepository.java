package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Questions, Long> {
}