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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private Long userId;
//    @Column(nullable = false)
//    private String firstName;
//
//    @Column(nullable = false)
//    private String lastName;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;


    @Column(nullable = false)
    private String cardNumber;

//    @ElementCollection  // to create a separate table for the list of enrolled courses
//    private List<Long> enrolledCourseIds;

    @ElementCollection
    private Map<Long, CourseInfo> enrolledCourses = new HashMap<>();


    @ElementCollection
    private Map<Long, String> completedCourses = new HashMap<>();

    @ElementCollection
    private Map<Long, String> inProgressCourses = new HashMap<>();

//    @Embedded
//    private Notification notification;
}
