package org.example.testmentorbackend.services;

import org.example.testmentorbackend.model.entity.UserAttempt;

import java.util.List;

public interface UserAttemptService {
    UserAttempt addAttempt(UserAttempt userAttempt);
    List<UserAttempt> getAttempt();
    UserAttempt findById(Long Id);

}