package org.example.testmentorbackend.services;

import org.example.testmentorbackend.dto.VoteRequestDto;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.Vote;

public interface ModerationService {
    Vote vote(Long quizId, String mentorUsername, VoteRequestDto voteRequest);
    Quizzes recalcAndFinalizeIfNeeded(Long quizId);
}
