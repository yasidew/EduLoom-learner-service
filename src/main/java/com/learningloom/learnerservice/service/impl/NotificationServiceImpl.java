package com.learningloom.learnerservice.service.impl;


import com.learningloom.learnerservice.UIDGenerator;
import com.learningloom.learnerservice.entity.Notification;
import com.learningloom.learnerservice.enums.NotificationStatus;
import com.learningloom.learnerservice.repository.NotificationRepository;
import com.learningloom.learnerservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void sendNotification(String toEmail,
                                 String enrolledCourseId,
                                 String courseName
    ) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setText(enrolledCourseId);
            message.setSubject(courseName);
            mailSender.send(message);

            Notification notification = new Notification();
            notification.setId(UIDGenerator.generateEmailUID());
            notification.setToEmail(toEmail);
            notification.setCourseId(enrolledCourseId);
            notification.setCourseName(courseName);
            notification.setStatus(NotificationStatus.DELIVERED);
            notificationRepository.save(notification);

            System.out.println("Mail Sent Successfully to: " + toEmail + " for course: " + courseName + " with course id: " + enrolledCourseId);
        }
        catch (Exception e){
            Notification notification = new Notification();
            notification.setToEmail(toEmail);
            notification.setCourseId(enrolledCourseId);
            notification.setCourseName(courseName);
            notification.setStatus(NotificationStatus.valueOf(NotificationStatus.FAILED.toString()));
            notificationRepository.save(notification);

            System.out.println("Mail Sending Failed." + e.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<Notification> findNotificationById(String id) {
        return notificationRepository.findById(id);
    }
}
