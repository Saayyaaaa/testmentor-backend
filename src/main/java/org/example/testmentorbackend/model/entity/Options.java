package org.example.testmentorbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Options {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionID;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id")
    private Questions questions;

    private String optionText;

    @JsonIgnore // never send correct answers to the client
    private boolean isCorrect;
}
