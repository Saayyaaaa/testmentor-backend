package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.repository.UserRepository;
import org.example.testmentorbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
                .map(UserInfoDetailsImpl::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public String addUser(User userInfo) {
        if (userInfo.getRoles() == null || userInfo.getRoles().isBlank()) {
            userInfo.setRoles("ROLE_STUDENT");
        } else {
            userInfo.setRoles(normalizeRoles(userInfo.getRoles()));
        }
        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userRepository.save(userInfo);
        return "User Added Successfully";
    }

    @Override
    public User getByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("User not found: " + name));
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    public User setRole(Long userId, String role) {
        String normalized = normalizeRoles(role);
        User user = getById(userId);
        user.setRoles(normalized);
        return userRepository.save(user);
    }

    @Override
    public List<User> getByRole(String role) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRoles() != null && user.getRoles().contains(role))
                .toList();
    }

    @Override
    public void deleteUser(Long userId, String currentAdminName) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User currentAdmin = userRepository.findByName(currentAdminName)
                .orElseThrow(() -> new RuntimeException("Current admin not found"));

        if (target.getId().equals(currentAdmin.getId())) {
            throw new RuntimeException("Admin cannot delete own account");
        }

        if (target.getRoles() != null && target.getRoles().contains("ROLE_ADMIN")) {
            throw new RuntimeException("Admin cannot delete another admin");
        }

        userRepository.delete(target);
    }

    private String normalizeRoles(String raw) {
        String[] parts = raw.split(",");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            String r = p.trim();
            if (r.isEmpty()) continue;
            r = r.toUpperCase(Locale.ROOT);
            if (!r.startsWith("ROLE_")) r = "ROLE_" + r;
            if (sb.length() > 0) sb.append(",");
            sb.append(r);
        }
        if (sb.length() == 0) return "ROLE_STUDENT";
        return sb.toString();
    }
}
