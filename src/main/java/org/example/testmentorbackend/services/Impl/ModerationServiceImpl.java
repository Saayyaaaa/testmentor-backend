package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.dto.VoteRequestDto;
import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.model.entity.Vote;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.example.testmentorbackend.model.enums.VoteType;
import org.example.testmentorbackend.repository.QuizzesRepository;
import org.example.testmentorbackend.repository.UserRepository;
import org.example.testmentorbackend.repository.VoteRepository;
import org.example.testmentorbackend.services.ModerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final VoteRepository voteRepository;
    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;

    @Autowired
    public ModerationServiceImpl(VoteRepository voteRepository,
                                QuizzesRepository quizzesRepository,
                                UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Vote vote(Long quizId, String mentorUsername, VoteRequestDto voteRequest) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        if (quiz.getStatus() != TestStatus.PENDING) {
            throw new IllegalStateException("Quiz is not pending. Current status: " + quiz.getStatus());
        }

        User mentor = userRepository.findByName(mentorUsername)
                .orElseThrow(() -> new NotFoundException("Mentor not found: " + mentorUsername));

        if (mentor.getRoles() == null || !mentor.getRoles().contains("ROLE_MENTOR")) {
            throw new AccessDeniedException("Only mentors can vote");
        }

        voteRepository.findByQuiz_QuizIDAndMentor_Id(quizId, mentor.getId()).ifPresent(v -> {
            throw new IllegalStateException("You have already voted for this quiz");
        });

        Vote vote = new Vote();
        vote.setQuiz(quiz);
        vote.setMentor(mentor);
        vote.setVoteType(voteRequest.getVoteType());
        vote.setComment(voteRequest.getComment());

        voteRepository.save(vote);

        if (voteRequest.getVoteType() == VoteType.APPROVE) {
            quiz.setApprovalsCount(quiz.getApprovalsCount() + 1);
        } else {
            quiz.setRejectsCount(quiz.getRejectsCount() + 1);
        }

        quizzesRepository.save(quiz);
        recalcAndFinalizeIfNeeded(quizId);

        return vote;
    }

    @Override
    @Transactional
    public Quizzes recalcAndFinalizeIfNeeded(Long quizId) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        if (quiz.getStatus() != TestStatus.PENDING) {
            return quiz;
        }

        if (quiz.getApprovalsCount() >= quiz.getRequiredVotes()) {
            quiz.setStatus(TestStatus.APPROVED);
        } else if (quiz.getRejectsCount() >= quiz.getRequiredVotes()) {
            quiz.setStatus(TestStatus.REJECTED);
        }

        return quizzesRepository.save(quiz);
    }
}
