package com.learningloom.learnerservice.entity;

import com.learningloom.learnerservice.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "notifications")
public class Notification {
    @Id
    private String id;
    private String toEmail;
    private String courseId;
    private String courseName;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
}
