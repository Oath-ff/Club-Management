package com.project.pojo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int membershipId; // 成员ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // 用户ID

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club; // 社团ID

    private LocalDateTime joinDate; // 加入时间
    private String status; //状态
}
