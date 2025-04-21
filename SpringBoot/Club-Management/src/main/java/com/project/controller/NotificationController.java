package com.project.controller;

import com.project.pojo.Notification;
import com.project.pojo.Result;
import com.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")//发送通知
    public Result sendNotification(@RequestBody Notification notification) {

        notificationService.sendNotification(notification);
        return Result.success();
    }

    @GetMapping("/user/{userId}")//查询用户的所有通知
    public Result<List<Notification>> getUserNotifications(@PathVariable Integer userId) {

        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return Result.success(notifications);
    }
}
