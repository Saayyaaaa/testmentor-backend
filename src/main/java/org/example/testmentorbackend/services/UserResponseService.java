package org.example.testmentorbackend.services;

import org.example.testmentorbackend.model.entity.UserResponses;

import java.util.List;

public interface UserResponseService {
    UserResponses AddResponse(UserResponses userResponses);
    List<UserResponses> getAllResponse();
    UserResponses findById(Long Id);
}
