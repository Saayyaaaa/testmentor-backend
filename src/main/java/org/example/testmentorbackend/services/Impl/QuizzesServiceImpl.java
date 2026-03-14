package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.dto.MentorReviewQuizDto;
import org.example.testmentorbackend.dto.OptionDto;
import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.model.entity.Vote;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.example.testmentorbackend.repository.QuizzesRepository;
import org.example.testmentorbackend.repository.UserRepository;
import org.example.testmentorbackend.repository.VoteRepository;
import org.example.testmentorbackend.services.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public QuizzesServiceImpl(QuizzesRepository quizzesRepository,
                              UserRepository userRepository,
                              VoteRepository voteRepository) {
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public Quizzes createQuiz(QuizzesDto dto, String authorUsername) {
        User author = userRepository.findByName(authorUsername)
                .orElseThrow(() -> new NotFoundException("Author not found: " + authorUsername));

        Quizzes quiz = new Quizzes();
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTimeLimit(dto.getTimeLimit());
        quiz.setAuthor(author);
        quiz.setStatus(TestStatus.PENDING);
        quiz.setApprovalsCount(0);
        quiz.setRejectsCount(0);
        quiz.setRequiredVotes(dto.getRequiredVotes() != null ? dto.getRequiredVotes() : 6);

        if (dto.getQuestions() != null) {
            for (QuestionDto qd : dto.getQuestions()) {
                Questions q = new Questions();
                q.setQuestionText(qd.getQuestionText());
                q.setQuestionType(qd.getQuestionType());
                q.setAiAnswer(qd.getAiAnswer());
                q.setQuizzes(quiz);

                if (qd.getOptions() != null) {
                    for (OptionDto od : qd.getOptions()) {
                        Options opt = new Options();
                        opt.setOptionText(od.getOptionText());
                        opt.setCorrect(od.isCorrect());
                        opt.setQuestions(q);
                        q.getOptions().add(opt);
                    }
                }
                quiz.getQuestions().add(q);
            }
        }

        return quizzesRepository.save(quiz);
    }

    @Override
    public List<Quizzes> getAllQuizzesForAdmin() {
        return quizzesRepository.findAll();
    }

    @Override
    public List<Quizzes> getApprovedQuizzesForStudents() {
        return quizzesRepository.findAllByStatus(TestStatus.APPROVED);
    }

    @Override
    public List<Quizzes> getPendingQuizzesForMentors() {
        return quizzesRepository.findAllByStatus(TestStatus.PENDING);
    }

    @Override
    public Quizzes findById(Long id) {
        return quizzesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + id));
    }

    @Override
    public Quizzes findDetailsById(Long id) {
        return quizzesRepository.findDetailsById(id)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + id));
    }

    @Override
    public List<MentorReviewQuizDto> getReviewQuizzes(String username, boolean onlyMine, boolean isAdmin) {
        User currentUser = userRepository.findByName(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        List<Quizzes> quizzes = onlyMine
                ? quizzesRepository.findAllByAuthor_NameOrderByCreatedAtDesc(username)
                : quizzesRepository.findAll();

        List<MentorReviewQuizDto> out = new ArrayList<>();

        for (Quizzes q : quizzes) {
            Optional<Vote> myVote = voteRepository.findByQuiz_QuizIDAndMentor_Id(q.getQuizID(), currentUser.getId());

            int totalVotes = q.getApprovalsCount() + q.getRejectsCount();
            double approvalPercent = totalVotes == 0 ? 0.0 : (q.getApprovalsCount() * 100.0) / totalVotes;

            boolean isAuthor = q.getAuthor() != null && username.equals(q.getAuthor().getName());

            MentorReviewQuizDto dto = new MentorReviewQuizDto(
                    q.getQuizID(),
                    q.getTitle(),
                    q.getDescription(),
                    q.getStatus(),
                    q.getApprovalsCount(),
                    q.getRejectsCount(),
                    approvalPercent,
                    myVote.map(Vote::getVoteType).orElse(null),
                    q.getAuthor() != null ? q.getAuthor().getName() : null,
                    isAuthor || isAdmin,
                    isAuthor || isAdmin
            );

            out.add(dto);
        }

        return out;
    }

    @Override
    public List<MentorReviewQuizDto> getPendingQuizzesForMentorsWithMyVote(String username) {
        return getReviewQuizzes(username, false, false);
    }

    @Override
    public Quizzes updateQuizMeta(Long quizId, String username, boolean isAdmin, QuizzesDto dto) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        boolean isAuthor = quiz.getAuthor() != null && username.equals(quiz.getAuthor().getName());
        if (!isAuthor && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot edit this quiz");
        }

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            quiz.setTitle(dto.getTitle().trim());
        }

        if (dto.getDescription() != null) {
            quiz.setDescription(dto.getDescription().trim());
        }

        return quizzesRepository.save(quiz);
    }

    @Override
    public void deleteQuizForReviewPanel(Long quizId, String username, boolean isAdmin) {
        Quizzes quiz = quizzesRepository.findById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        boolean isAuthor = quiz.getAuthor() != null && username.equals(quiz.getAuthor().getName());
        if (!isAuthor && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("You cannot delete this quiz");
        }

        quizzesRepository.delete(quiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        if (!quizzesRepository.existsById(id)) {
            throw new NotFoundException("Quiz not found: " + id);
        }
        quizzesRepository.deleteById(id);
    }
}