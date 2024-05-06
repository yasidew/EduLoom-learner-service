package com.learningloom.learnerservice.repository;

import com.learningloom.learnerservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findByToEmail(String toEmail);
}
