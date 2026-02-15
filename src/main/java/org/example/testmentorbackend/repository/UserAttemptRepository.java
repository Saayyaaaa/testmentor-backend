package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.UserAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAttemptRepository extends JpaRepository<UserAttempt, Long> {

}