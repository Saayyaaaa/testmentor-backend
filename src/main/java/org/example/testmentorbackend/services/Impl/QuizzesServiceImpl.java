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
                        opt.setCorrect(od.isIsCorrect());
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
    public List<MentorReviewQuizDto> getPendingQuizzesForMentorsWithMyVote(String mentorUsername) {
        User mentor = userRepository.findByName(mentorUsername)
                .orElseThrow(() -> new NotFoundException("Mentor not found: " + mentorUsername));

        List<Quizzes> pending = quizzesRepository.findAllByStatus(TestStatus.PENDING);
        List<MentorReviewQuizDto> out = new ArrayList<>();

        for (Quizzes q : pending) {
            Optional<Vote> myVote = voteRepository.findByQuiz_QuizIDAndMentor_Id(q.getQuizID(), mentor.getId());
            MentorReviewQuizDto dto = new MentorReviewQuizDto(
                    q.getQuizID(),
                    q.getTitle(),
                    q.getDescription(),
                    q.getStatus(),
                    q.getApprovalsCount(),
                    q.getRejectsCount(),
                    myVote.map(Vote::getVoteType).orElse(null)
            );
            out.add(dto);
        }

        return out;
    }

    @Override
    public void deleteQuiz(Long id) {
        if (!quizzesRepository.existsById(id)) {
            throw new NotFoundException("Quiz not found: " + id);
        }
        quizzesRepository.deleteById(id);
    }
}