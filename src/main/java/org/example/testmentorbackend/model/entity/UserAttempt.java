package org.example.testmentorbackend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptID;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quizzes quizzes;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int score;
}
