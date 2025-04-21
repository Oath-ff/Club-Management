package com.project.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId; // 通知ID

    @NotEmpty(message = "Title is required")
    private String title; // 通知标题

    private String content; // 通知内容
    private LocalDateTime date; // 通知日期

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club; // 社团ID

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender; // 发送者ID

    @ManyToOne
    @NotNull
    @JoinColumn(name = "recipient_id")
    private User recipient; // 接收者ID
}
