package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.*;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.UserAttempt;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.example.testmentorbackend.services.QuizzesService;
import org.example.testmentorbackend.services.UserAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
//@CrossOrigin("*")
public class QuizzesController {

    private final QuizzesService quizzesService;
    private final UserAttemptService userAttemptService;

    public QuizzesController(QuizzesService quizzesService, UserAttemptService userAttemptService) {
        this.quizzesService = quizzesService;
        this.userAttemptService = userAttemptService;
    }

    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    @PostMapping
    public ResponseEntity<Quizzes> create(@RequestBody QuizzesDto dto, Authentication authentication) {
        String username = authentication.getName();
        Quizzes created = quizzesService.createQuiz(dto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Quizzes>> list(Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        if (isAdmin) {
            return ResponseEntity.ok(quizzesService.getAllQuizzesForAdmin());
        }
        // students + mentors see approved quizzes
        return ResponseEntity.ok(quizzesService.getApprovedQuizzesForStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quizzes> details(@PathVariable Long id, Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isMentor = hasRole(authentication, "ROLE_MENTOR");

        Quizzes quiz = quizzesService.findDetailsById(id);

        if (!isAdmin && !isMentor && quiz.getStatus() != TestStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Quiz is not approved yet");
        }

        return ResponseEntity.ok(quiz);
    }

    // Submit answers and save attempt
    @PostMapping("/{id}/submit")
    public ResponseEntity<QuizSubmitResultDto> submit(
            @PathVariable Long id,
            @RequestBody QuizSubmitRequestDto req,
            Authentication authentication
    ) {
        Quizzes quiz = quizzesService.findDetailsById(id);

        if (quiz.getStatus() != TestStatus.APPROVED && !hasRole(authentication, "ROLE_MENTOR") && !hasRole(authentication, "ROLE_ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Quiz is not approved yet");
        }

        Map<Long, Long> answers = req != null ? req.getAnswers() : null;
        int total = quiz.getQuestions() == null ? 0 : quiz.getQuestions().size();
        int correct = 0;

        if (answers != null && quiz.getQuestions() != null) {
            for (Questions q : quiz.getQuestions()) {
                Long selectedOptionId = answers.get(q.getQuestionID());
                if (selectedOptionId == null || q.getOptions() == null) continue;
                for (Options opt : q.getOptions()) {
                    if (opt.getOptionID().equals(selectedOptionId) && opt.isCorrect()) {
                        correct++;
                        break;
                    }
                }
            }
        }

        int score = correct; // 1 point per correct

        UserAttemptDto attemptDto = new UserAttemptDto();
        attemptDto.setQuizId(id);
        attemptDto.setScore(score);
        attemptDto.setTotalQuestions(total);
        attemptDto.setCorrectAnswers(correct);
        attemptDto.setStartTime(LocalDateTime.now());
        attemptDto.setEndTime(LocalDateTime.now());

        UserAttempt saved = userAttemptService.saveAttempt(authentication.getName(), attemptDto);

        return ResponseEntity.ok(new QuizSubmitResultDto(saved.getAttemptID(), score, total, correct));
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
