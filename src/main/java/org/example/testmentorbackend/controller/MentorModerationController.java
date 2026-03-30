package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.*;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.Vote;
import org.example.testmentorbackend.services.ModerationService;
import org.example.testmentorbackend.services.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor")
public class MentorModerationController {

    private final QuizzesService quizzesService;
    private final ModerationService moderationService;

    @Autowired
    public MentorModerationController(QuizzesService quizzesService, ModerationService moderationService) {
        this.quizzesService = quizzesService;
        this.moderationService = moderationService;
    }

    @GetMapping("/review")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<List<MentorReviewQuizDto>> review(
            @RequestParam(defaultValue = "all") String scope,
            Authentication authentication
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean onlyMine = "mine".equalsIgnoreCase(scope);

        return ResponseEntity.ok(
                quizzesService.getReviewQuizzes(authentication.getName(), onlyMine, isAdmin)
        );
    }

    @PostMapping("/review/{quizId}/vote")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<VoteResponseDto> vote(
            @PathVariable Long quizId,
            @RequestBody VoteRequestDto voteRequest,
            Authentication authentication
    ) {
        Vote vote = moderationService.vote(quizId, authentication.getName(), voteRequest);

        VoteResponseDto response = new VoteResponseDto(
                vote.getId(),
                vote.getVoteType(),
                vote.getComment(),
                vote.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/review/{quizId}")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<QuizMetaResponseDto> updateQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizzesDto dto,
            Authentication authentication
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        Quizzes updated = quizzesService.updateQuizMeta(quizId, authentication.getName(), isAdmin, dto);

        QuizMetaResponseDto response = new QuizMetaResponseDto(
                updated.getQuizID(),
                updated.getTitle(),
                updated.getDescription(),
                updated.getTimeLimit(),
                updated.getStatus(),
                updated.getApprovalsCount(),
                updated.getRejectsCount(),
                updated.getRequiredVotes()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/{quizId}")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<MentorReviewDetailsDto> reviewDetails(
            @PathVariable Long quizId,
            Authentication authentication
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");

        return ResponseEntity.ok(
                quizzesService.getReviewQuizDetails(quizId, authentication.getName(), isAdmin)
        );
    }

    @DeleteMapping("/review/{quizId}")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable Long quizId,
            Authentication authentication
    ) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        quizzesService.deleteQuizForReviewPanel(quizId, authentication.getName(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}