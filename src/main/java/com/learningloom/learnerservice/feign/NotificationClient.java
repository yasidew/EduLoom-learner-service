package com.learningloom.learnerservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.management.Notification;
import java.util.Optional;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody Notification notification);

    @GetMapping("/{id}")
    public  ResponseEntity<Optional<Notification>> getNotificationById(@PathVariable String id);

}
