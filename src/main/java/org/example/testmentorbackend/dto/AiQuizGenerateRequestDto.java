package org.example.testmentorbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiQuizGenerateRequestDto {
    private String topic;
    private String sourceText;
    private Integer questionCount;
    private String difficulty;
    private String language;
}