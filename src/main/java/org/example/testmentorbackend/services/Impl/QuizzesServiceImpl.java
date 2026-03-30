package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.dto.MentorReviewDetailsDto;
import org.example.testmentorbackend.dto.MentorReviewQuizDto;
import org.example.testmentorbackend.dto.OptionDto;
import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.dto.VoteCommentDto;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.testmentorbackend.dto.AiAppendQuestionsRequestDto;
import org.example.testmentorbackend.services.AiQuizService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;
    private final AiQuizService aiQuizService;

    @Autowired
    public QuizzesServiceImpl(QuizzesRepository quizzesRepository,
                              UserRepository userRepository,
                              VoteRepository voteRepository,
                              AiQuizService aiQuizService) {
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
        this.aiQuizService = aiQuizService;
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
                    myVote.map(Vote::getComment).orElse(null),
                    q.getAuthor() != null ? q.getAuthor().getName() : null,
                    isAuthor || isAdmin,
                    isAuthor || isAdmin
            );

            out.add(dto);
        }

        return out;
    }

    @Override
    @Transactional(readOnly = true)
    public MentorReviewDetailsDto getReviewQuizDetails(Long quizId, String username, boolean isAdmin) {
        User currentUser = userRepository.findByName(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Quizzes quiz = quizzesRepository.findDetailsById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        Optional<Vote> myVote = voteRepository.findByQuiz_QuizIDAndMentor_Id(quiz.getQuizID(), currentUser.getId());

        List<VoteCommentDto> comments = voteRepository.findAllDetailedByQuizId(quizId)
                .stream()
                .map(v -> new VoteCommentDto(
                        v.getId(),
                        v.getMentor() != null ? v.getMentor().getName() : "Unknown",
                        v.getVoteType(),
                        v.getComment(),
                        v.getCreatedAt()
                ))
                .toList();

        List<QuestionDto> questions = mapQuestions(quiz.getQuestions());

        int totalVotes = quiz.getApprovalsCount() + quiz.getRejectsCount();
        double approvalPercent = totalVotes == 0 ? 0.0 : (quiz.getApprovalsCount() * 100.0) / totalVotes;
        boolean isAuthor = quiz.getAuthor() != null && username.equals(quiz.getAuthor().getName());

        return new MentorReviewDetailsDto(
                quiz.getQuizID(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getStatus(),
                quiz.getApprovalsCount(),
                quiz.getRejectsCount(),
                approvalPercent,
                myVote.map(Vote::getVoteType).orElse(null),
                myVote.map(Vote::getComment).orElse(null),
                quiz.getAuthor() != null ? quiz.getAuthor().getName() : null,
                isAuthor || isAdmin,
                isAuthor || isAdmin,
                questions,
                comments
        );
    }

    @Override
    @Transactional
    public Quizzes appendAiQuestions(Long quizId, String username, boolean isAdmin, AiAppendQuestionsRequestDto request) {
        Quizzes quiz = quizzesRepository.findDetailsById(quizId)
                .orElseThrow(() -> new NotFoundException("Quiz not found: " + quizId));

        boolean isAuthor = quiz.getAuthor() != null && username.equals(quiz.getAuthor().getName());
        if (!isAuthor && !isAdmin) {
            throw new AccessDeniedException("You cannot edit this quiz");
        }

        List<QuestionDto> generatedQuestions = aiQuizService.generateAdditionalQuestions(
                request,
                quiz.getTitle(),
                quiz.getDescription()
        );

        for (QuestionDto qd : generatedQuestions) {
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

        quiz.setStatus(TestStatus.PENDING);
        quiz.setApprovalsCount(0);
        quiz.setRejectsCount(0);

        return quizzesRepository.save(quiz);
    }

    private List<QuestionDto> mapQuestions(List<Questions> entities) {
        List<QuestionDto> questions = new ArrayList<>();
        if (entities == null) {
            return questions;
        }

        for (Questions q : entities) {
            List<OptionDto> options = new ArrayList<>();
            if (q.getOptions() != null) {
                for (Options opt : q.getOptions()) {
                    options.add(new OptionDto(
                            opt.getOptionID(),
                            q.getQuestionID(),
                            opt.getOptionText(),
                            opt.isCorrect()
                    ));
                }
            }

            questions.add(new QuestionDto(
                    q.getQuestionID(),
                    q.getQuestionText(),
                    q.getQuestionType(),
                    q.getAiAnswer(),
                    options,
                    q.getQuizzes() != null ? q.getQuizzes().getQuizID() : null
            ));
        }

        return questions;
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
            throw new AccessDeniedException("You cannot edit this quiz");
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
            throw new AccessDeniedException("You cannot delete this quiz");
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
