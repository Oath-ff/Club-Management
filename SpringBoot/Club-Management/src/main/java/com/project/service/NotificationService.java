package com.project.service;

import com.project.pojo.Notification;

import java.util.List;

public interface NotificationService {
    void sendNotification(Notification notification);

    List<Notification> getUserNotifications(Integer userId);
}
