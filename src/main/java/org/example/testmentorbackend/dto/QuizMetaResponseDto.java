package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.testmentorbackend.model.enums.TestStatus;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizMetaResponseDto {
    private Long quizID;
    private String title;
    private String description;
    private LocalTime timeLimit;
    private TestStatus status;
    private int approvalsCount;
    private int rejectsCount;
    private int requiredVotes;
}