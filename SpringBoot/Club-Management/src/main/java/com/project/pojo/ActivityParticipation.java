package com.project.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_participation")
public class ActivityParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int participationId; // 活动参与记录ID

    @ManyToOne
    @JoinColumn(name = "membership_id")
    private Membership membership; // 参与会员

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event; // 参与活动

    private LocalDateTime participationDate; // 参与日期

    @Enumerated(EnumType.STRING)
    private Status status; // 参与状态

    public enum Status {
        报名, 签到
    }
}
