package org.example.testmentorbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.example.testmentorbackend.model.enums.VoteType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MentorReviewQuizDto {
    private Long quizID;
    private String tittle;
    private String description;
    private TestStatus status;
    private int approvalsCount;
    private int rejectsCount;

    private VoteType myVote;
}