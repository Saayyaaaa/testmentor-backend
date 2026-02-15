package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.repository.UserAttemptRepository;
import org.example.testmentorbackend.services.UserAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAttemptServiceImpl implements UserAttemptService {

    private final UserAttemptRepository userAttemptRepository;

    @Autowired
    public UserAttemptServiceImpl(UserAttemptRepository userAttemptRepository) {
        this.userAttemptRepository = userAttemptRepository;
    }

    @Override
    public UserAttempt addAttempt(UserAttempt userAttempt) {
        return userAttemptRepository.save(userAttempt);
    }

    @Override
    public List<UserAttempt> getAttempt() {
        return userAttemptRepository.findAll();
    }

    @Override
    public UserAttempt findById(Long id) {
        return userAttemptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Attempt not found: " + id));
    }
}
