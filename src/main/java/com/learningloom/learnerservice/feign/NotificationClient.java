package com.learningloom.learnerservice.feign;

import com.learningloom.learnerservice.entity.Notification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "notificationservice", url = "http://localhost:8082/api/v1/notifications")
public interface NotificationClient {

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody Notification notification);

    @GetMapping("/{id}")
    public  ResponseEntity<Optional<Notification>> getNotificationById(@PathVariable String id);

}
