package org.example.testmentorbackend.services.Impl;

import org.example.testmentorbackend.dto.OptionDto;
import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.exceptions.NotFoundException;
import org.example.testmentorbackend.model.entity.Options;
import org.example.testmentorbackend.model.entity.Questions;
import org.example.testmentorbackend.model.entity.Quizzes;
import org.example.testmentorbackend.model.entity.User;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.example.testmentorbackend.repository.QuizzesRepository;
import org.example.testmentorbackend.repository.UserRepository;
import org.example.testmentorbackend.services.QuizzesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizzesServiceImpl implements QuizzesService {

    private final QuizzesRepository quizzesRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizzesServiceImpl(QuizzesRepository quizzesRepository, UserRepository userRepository) {
        this.quizzesRepository = quizzesRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Quizzes createQuiz(QuizzesDto dto, String authorUsername) {
        User author = userRepository.findByName(authorUsername)
                .orElseThrow(() -> new NotFoundException("Author not found: " + authorUsername));

        Quizzes quiz = new Quizzes();
        quiz.setTittle(dto.getTittle());
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
    public void deleteQuiz(Long id) {
        if (!quizzesRepository.existsById(id)) {
            throw new NotFoundException("Quiz not found: " + id);
        }
        quizzesRepository.deleteById(id);
    }
}
