package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.Util.JwtUtil;
import org.example.testmentorbackend.dto.AuthRequestDto;
import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.services.Impl.UserServiceImpl;
import org.example.testmentorbackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin("*") //Разрешает frontend обращаться с любого домена. без этого браузер может блокировать запросы (CORS)

public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody User userInfo) {
        return userService.addUser(userInfo);
    }

    @PostMapping(value="/signing", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDto authRequestDto) {

        Authentication authentication = authenticationManager.authenticate //создать authentication запрос
                (new UsernamePasswordAuthenticationToken(
                        authRequestDto.getName(),
                        authRequestDto.getPassword()));

        if (authentication.isAuthenticated()) {
            User user = userService.getByName(authRequestDto.getName());

            Map<String, String> response = new HashMap<>();
            response.put("token", jwtService.generateToken(authRequestDto.getName(), user.getRoles()));
            return ResponseEntity.ok(response);
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }
}