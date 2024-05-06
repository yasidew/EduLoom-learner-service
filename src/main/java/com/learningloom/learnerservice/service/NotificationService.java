package com.learningloom.learnerservice.service;


import com.learningloom.learnerservice.entity.Notification;

import java.util.Optional;

public interface NotificationService {
    void sendNotification(String toEmail, String enrolledCourseId, String courseName);

    Optional<Notification> findNotificationById(String id);
}
