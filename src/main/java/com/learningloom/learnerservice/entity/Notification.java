package com.learningloom.learnerservice.entity;

import com.learningloom.learnerservice.enums.NotificationStatus;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Notification {
    private String id;
    private String toEmail;
    private String courseId;
    private String courseName;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
}
