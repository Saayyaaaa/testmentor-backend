package org.example.testmentorbackend.controller;

import org.example.testmentorbackend.dto.MentorReviewQuizDto;
import org.example.testmentorbackend.dto.VoteRequestDto;
import org.example.testmentorbackend.model.entity.Vote;
import org.example.testmentorbackend.services.ModerationService;
import org.example.testmentorbackend.services.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<List<MentorReviewQuizDto>> pending(Authentication authentication) {
        return ResponseEntity.ok(
                quizzesService.getPendingQuizzesForMentorsWithMyVote(authentication.getName())
        );
    }

    @PostMapping("/review/{quizId}/vote")
    @PreAuthorize("hasAnyRole('MENTOR','ADMIN')")
    public ResponseEntity<Vote> vote(@PathVariable Long quizId,
                                     @RequestBody VoteRequestDto voteRequest,
                                     Authentication authentication) {
        Vote vote = moderationService.vote(quizId, authentication.getName(), voteRequest);
        return ResponseEntity.ok(vote);
    }
}