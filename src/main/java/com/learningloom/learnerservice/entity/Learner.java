package com.learningloom.learnerservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "learners")
public class Learner {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String cardNumber;


    @ElementCollection
    private Map<Long, CourseInfo> enrolledCourses = new HashMap<>();


    @ElementCollection
    private Map<Long, String> completedCourses = new HashMap<>();

    @ElementCollection
    private Map<Long, String> inProgressCourses = new HashMap<>();
}
