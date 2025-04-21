package com.project.service.impl;

import com.project.mapper.NotificationMapper;
import com.project.pojo.Notification;
import com.project.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void sendNotification(Notification notification) {
        notificationMapper.addNotification(notification);
    }

    @Override
    public List<Notification> getUserNotifications(Integer userId) {
        return notificationMapper.getUserNotifications(userId);
    }
}
