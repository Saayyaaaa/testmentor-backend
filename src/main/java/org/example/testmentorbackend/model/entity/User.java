package org.example.testmentorbackend.model.entity;

import jakarta.persistence.*; //JPA - управление базой данных через объекты
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp; //Hibernate -реализация JPA, отслеживает за временем
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id //PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Авто-генерация id в базе
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;
    private String contact;
    private String roles;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
