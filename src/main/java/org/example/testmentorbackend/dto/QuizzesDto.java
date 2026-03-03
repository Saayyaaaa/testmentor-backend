package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizzesDto {
    private Long quizID;
    private String title;
    private String description;
    private LocalTime timeLimit;
    private Integer requiredVotes;

    private List<QuestionDto> questions;
}
