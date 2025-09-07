package com.example.campusmate.entity;

import jakarta.persistence.*; // JPA注解包
import lombok.Data; // Lombok自动生成getter/setter等
import java.time.LocalDateTime; // Java8时间类

@Data // Lombok注解，自动生成常用方法
@Entity // JPA注解，标记为实体类
@Table(name = "activities") // 指定表名
public class Activity {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id; // 活动ID

    @Column(nullable = false) // 非空，创建者ID
    private Long creatorId; // 创建者用户ID

    @Column(nullable = false) // 非空，活动标题
    private String title; // 活动标题

    @Column(nullable = false, columnDefinition = "TEXT") // 非空，活动描述
    private String description; // 活动描述

    @Column(nullable = false) // 非空，活动类型
    private String type; // 活动类型

    @Column(nullable = false) // 非空，活动时间
    private LocalDateTime activityTime; // 活动时间

    @Column(nullable = false) // 非空，活动地点
    private String location; // 活动地点

    @Column(nullable = false) // 非空，最低参与人数
    private Integer minPeople; // 最低参与人数

    @Column(nullable = false) // 非空，最高参与人数
    private Integer maxPeople; // 最高参与人数

    @Column(nullable = false) // 非空，活动截止时间
    private LocalDateTime expireTime; // 活动截止时间

    @Column(nullable = false) // 非空，校区
    private String campus; // 活动关联校区

    private String college; // 活动关联学院
    private String tags; // 活动标签（逗号分隔）

    @Column(nullable = false) // 非空，活动状态
    private String status; // 状态（PUBLISHED/DRAFT/EXPIRED）

    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 更新时间

    @Column(name = "image_url")
    private String imageUrl;
}