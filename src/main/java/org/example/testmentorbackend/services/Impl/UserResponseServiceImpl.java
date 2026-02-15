package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.UserResponses;
import org.example.testmentorbackend.repository.UserResponseRepository;
import org.example.testmentorbackend.services.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserResponseServiceImpl implements UserResponseService {

    private final UserResponseRepository userResponseRepository;

    @Autowired
    public UserResponseServiceImpl(UserResponseRepository userResponseRepository) {
        this.userResponseRepository = userResponseRepository;
    }

    @Override
    public UserResponses AddResponse(UserResponses userResponses) {
        return userResponseRepository.save(userResponses);
    }

    @Override
    public List<UserResponses> getAllResponse() {
        return userResponseRepository.findAll();
    }

    @Override
    public UserResponses findById(Long id) {
        return userResponseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Response not found: " + id));
    }
}
