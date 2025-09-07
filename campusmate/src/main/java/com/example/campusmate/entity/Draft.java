package com.example.campusmate.entity;

import jakarta.persistence.*; // JPA注解包
import lombok.Data; // Lombok自动生成getter/setter等
import java.time.LocalDateTime; // Java8时间类

@Data // Lombok注解，自动生成常用方法
@Entity // JPA注解，标记为实体类
@Table(name = "drafts") // 指定表名
public class Draft {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id; // 草稿ID

    @Column(nullable = false) // 非空，所属用户ID
    private Long userId; // 所属用户ID

    @Column(unique = true) // 唯一，关联活动ID
    private Long activityId; // 关联活动ID

    @Column(nullable = false) // 非空，进入草稿箱原因
    private String reason; // 进入草稿箱原因（EXPIRED/DELETED）

    private LocalDateTime createdAt; // 进入草稿箱时间

    // 活动所有字段
    private String title;
    private String description;
    private String type;
    private LocalDateTime activityTime;
    private String location;
    private Integer minPeople;
    private Integer maxPeople;
    private LocalDateTime expireTime;
    private String campus;
    private String college;
    private String tags;
    private String status;
    private LocalDateTime updatedAt;
    private String imageUrl;
}