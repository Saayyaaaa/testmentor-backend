package org.example.testmentorbackend.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.testmentorbackend.model.enums.TestStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quizzes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizID;

    private String title;
    private String description;
    private LocalTime timeLimit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
//    @JsonIgnore // do not expose user details (password/roles) in quiz JSON
    private User author;

    @Enumerated(EnumType.STRING)
    private TestStatus status = TestStatus.PENDING;

    private int approvalsCount = 0;
    private int rejectsCount = 0;

    /**
     * порог для автоматического принятия решения дефолтный 6
     */
    private int requiredVotes = 6;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "quizzes", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Questions> questions = new ArrayList<>();
}
