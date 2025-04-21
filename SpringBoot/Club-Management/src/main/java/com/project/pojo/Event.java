package com.project.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @NotBlank(message = "活动名称不能为空")
    @Size(max = 100, message = "活动名称不能超过100个字符")
    private String name;

    @NotNull(message = "活动日期不能为空")
    private LocalDateTime date;

    @NotBlank(message = "活动地点不能为空")
    @Size(max = 100, message = "活动地点不能超过100个字符")
    private String location;

    @Size(max = 255, message = "活动描述不能超过255个字符")
    private String description;

    @Size(max = 20, message = "活动状态不能超过20个字符")
    private String status = "待审核";
}
