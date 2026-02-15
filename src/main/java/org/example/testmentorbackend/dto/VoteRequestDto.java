package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.testmentorbackend.model.enums.VoteType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteRequestDto {
    private VoteType voteType;
    private String comment;
}
