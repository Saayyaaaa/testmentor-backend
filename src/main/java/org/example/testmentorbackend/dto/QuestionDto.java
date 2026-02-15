package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private Long QuestionID;
    private String QuestionText;
    private String QuestionType;

    private String AiAnswer;

    private Long QuizId;
    private List<OptionDto> options;
}
