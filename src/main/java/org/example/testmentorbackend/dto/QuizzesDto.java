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
    private Long QuizID;
    private String Tittle;
    private String Description;
    private LocalTime TimeLimit;
    private Integer requiredVotes;

    private List<QuestionDto> questions;
}
