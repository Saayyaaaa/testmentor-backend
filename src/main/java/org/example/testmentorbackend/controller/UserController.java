package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.UserProfileDto;
import org.example.testmentorbackend.dto.UserProfileUpdateDto;
import org.example.testmentorbackend.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getMyProfile(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileDto> updateMe(
            @RequestBody UserProfileUpdateDto dto,
            Authentication authentication
    ) {
        return ResponseEntity.ok(userService.updateMyProfile(authentication.getName(), dto));
    }
}