package com.example.campusmate.entity;

import jakarta.persistence.*; // JPA注解包
import lombok.Data; // Lombok自动生成getter/setter等
import java.time.LocalDateTime; // Java8时间类

@Data // Lombok注解，自动生成常用方法
@Entity // JPA注解，标记为实体类
@Table(name = "user_info") // 指定表名
public class UserInfo {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id; // 表主键

    @Column(nullable = false, unique = true) // 唯一且非空，App用户ID
    private Long userId; // App主用户ID

    @Column(nullable = false) // 非空，学号
    private String studentId; // 学号

    // 在 UserInfo.java 实体类中确保字段定义如下
    @Column(name = "campus", nullable = true)
    private String campus;

    @Column(name = "college", nullable = true)
    private String college;

    @Column(name = "major", nullable = true)
    private String major;

    @Column(name = "grade", nullable = true)
    private String grade;

    @Column(name = "gender", nullable = true)
    private String gender;

    @Column(name = "avatar_url")
    private String avatarUrl; // 头像URL
    private String signature; // 个性签名
    private String interests; // 兴趣爱好（逗号分隔）
    private String skills; // 技能特长（逗号分隔）

    private String qq; // QQ号
    private String wechat; // 微信号
    private String phone; // 手机号
    private String contactVisibility; // 联系方式可见性设置（如 qq,wechat,phone）
    private String infoVisibility; // 信息可见性设置

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间
}