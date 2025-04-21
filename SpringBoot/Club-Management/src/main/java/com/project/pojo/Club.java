package com.project.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "clubs")
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int clubId; // 社团ID

    @NotBlank(message = "社团名称不能为空")
    @Size(max = 10, message = "社团名称不能超过10个字符")
    private String name; // 社团名称

    @Size(max = 10, message = "社团类型不能超过10个字符")
    private String type; // 社团类型

    @ManyToOne
    @JoinColumn(name = "leader_id")
    private User leader; // 社团团长

    @Size(max = 255, message = "社团描述不能超过255个字符")
    private String description; // 社团描述

    @NotBlank(message = "社团状态不能为空")
    private String status = "待审核"; // 社团状态，默认为"待审核"
}
