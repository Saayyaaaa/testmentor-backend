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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

        User mentor = userRepository.findByName(mentorUsername)
                .orElseThrow(() -> new NotFoundException("Mentor not found: " + mentorUsername));

        Optional<Vote> existingVoteOpt = voteRepository.findByQuiz_QuizIDAndMentor_Id(quizId, mentor.getId());

        Vote vote;
        if (existingVoteOpt.isPresent()) {
            vote = existingVoteOpt.get();

            if (vote.getVoteType() != voteRequest.getVoteType()) {
                // убираем старый голос из счетчиков
                if (vote.getVoteType() == VoteType.APPROVE) {
                    quiz.setApprovalsCount(Math.max(0, quiz.getApprovalsCount() - 1));
                } else if (vote.getVoteType() == VoteType.REJECT) {
                    quiz.setRejectsCount(Math.max(0, quiz.getRejectsCount() - 1));
                }

                // ставим новый голос
                vote.setVoteType(voteRequest.getVoteType());

                if (voteRequest.getVoteType() == VoteType.APPROVE) {
                    quiz.setApprovalsCount(quiz.getApprovalsCount() + 1);
                } else if (voteRequest.getVoteType() == VoteType.REJECT) {
                    quiz.setRejectsCount(quiz.getRejectsCount() + 1);
                }
            }
        } else {
            vote = new Vote();
            vote.setQuiz(quiz);
            vote.setMentor(mentor);
            vote.setVoteType(voteRequest.getVoteType());

            if (voteRequest.getVoteType() == VoteType.APPROVE) {
                quiz.setApprovalsCount(quiz.getApprovalsCount() + 1);
            } else if (voteRequest.getVoteType() == VoteType.REJECT) {
                quiz.setRejectsCount(quiz.getRejectsCount() + 1);
            }
        }

        vote.setComment(voteRequest.getComment() == null ? null : voteRequest.getComment().trim());

        recalculateQuizStatus(quiz);

        quizzesRepository.save(quiz);
        return voteRepository.save(vote);
    }

    @Override
    @Transactional
    public Quizzes recalcAndFinalizeIfNeeded(Long quizId) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        recalculateQuizStatus(quiz);
        return quizzesRepository.save(quiz);
    }

    private void recalculateQuizStatus(Quizzes quiz) {
        int totalVotes = quiz.getApprovalsCount() + quiz.getRejectsCount();

        if (totalVotes < quiz.getRequiredVotes()) {
            quiz.setStatus(TestStatus.PENDING);
            return;
        }

        double approvalPercent = (quiz.getApprovalsCount() * 100.0) / totalVotes;

        if (approvalPercent >= 60.0) {
            quiz.setStatus(TestStatus.APPROVED);
        } else {
            // теперь тест не отклоняется окончательно, а возвращается в ожидание голосования
            quiz.setStatus(TestStatus.PENDING);
        }
    }
}