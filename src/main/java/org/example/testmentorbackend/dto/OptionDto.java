package org.example.testmentorbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDto {
    private Long optionID;
    private Long questionId;
    private String optionText;
    @JsonProperty("isCorrect")
    private boolean correct;
}
