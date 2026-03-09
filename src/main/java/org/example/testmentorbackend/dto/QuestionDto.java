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
    private Long questionID;
    private String questionText;
    private String questionType;
    private String aiAnswer;
    private List<OptionDto> options;
    private Long quizId;
}
