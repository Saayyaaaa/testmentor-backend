package org.example.testmentorbackend.services;

import org.example.testmentorbackend.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    @Override
    UserDetails loadUserByUsername(String username);

    String addUser(User user);

    User getByName(String name);

    User getById(Long id);

    User setRole(Long userId, String role);
}
