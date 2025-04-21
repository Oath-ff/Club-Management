package com.project.mapper;

import com.project.pojo.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    // 添加通知
    @Insert("INSERT INTO notifications(title, content, date, club_id, sender_id, recipient_id) VALUES(#{title}, #{content}, #{date}, #{club.clubId}, #{sender.userId}, #{recipient.userId})")
    void addNotification(Notification notification);

    // 查询用户的所有通知
    @Select("SELECT * FROM notifications WHERE recipient_id = #{userId}")
    @Results({
            @Result(property = "notificationId", column = "notification_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "content", column = "content"),
            @Result(property = "date", column = "date"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "sender.userId", column = "sender_id"),
            @Result(property = "recipient.userId", column = "recipient_id")
    })
    List<Notification> getUserNotifications(Integer userId);
}
