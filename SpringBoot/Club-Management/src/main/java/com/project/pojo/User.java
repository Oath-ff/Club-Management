package com.project.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private int userId; // 用户ID

    @NotEmpty(message = "Username is required")
    private String username; // 用户名

    @NotEmpty(message = "Password is required")
    @JsonIgnore // 转换为 JSON 时忽略密码
    private String password; // 密码

    @NotEmpty(message = "Role is required")
    private String role; // 角色

    private String userPic; // 用户头像路径

    private String nickname; // 昵称

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{11}$", message = "Phone number must be 11 digits")
    private String phoneNumber; // 联系电话，必须为11位数字
}
