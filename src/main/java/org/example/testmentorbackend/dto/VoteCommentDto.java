package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.testmentorbackend.model.enums.VoteType;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoteCommentDto {
    private Long voteId;
    private String mentorName;
    private VoteType voteType;
    private String comment;
    private LocalDateTime createdAt;
}
