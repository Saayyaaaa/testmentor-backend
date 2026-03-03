package org.example.testmentorbackend.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Questions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionID;

    @Column(length = 2000)
    private String questionText;

    private String questionType;

    @Column(length = 4000)
    private String aiAnswer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quizzes quizzes;

    @OneToMany(mappedBy = "questions", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 50)
    private List<Options> options = new ArrayList<>();
}
