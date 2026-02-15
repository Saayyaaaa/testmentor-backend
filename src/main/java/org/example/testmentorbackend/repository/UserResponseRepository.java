package org.example.testmentorbackend.repository;

import org.example.testmentorbackend.model.entity.UserResponses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResponseRepository extends JpaRepository<UserResponses, Long> {
}