package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.UserProfileDto;
import org.example.testmentorbackend.dto.UserProfileUpdateDto;
import org.example.testmentorbackend.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);

    String addUser(User user);

    User getByName(String name);

    User getById(Long id);

    User setRole(Long userId, String role);

    List<User> getByRole(String role);

    void deleteUser(Long userId, String currentAdminName);

    UserProfileDto getMyProfile(String username);

    UserProfileDto updateMyProfile(String username, UserProfileUpdateDto dto);
}