package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAttemptDto {
    private Long AttemptID;
    private Long QuizId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int score;
}
