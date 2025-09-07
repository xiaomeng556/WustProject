package com.example.campusmate.entity;

import jakarta.persistence.*; // JPA注解包
import lombok.Data; // Lombok自动生成getter/setter等
import java.time.LocalDateTime; // Java8时间类

@Data // Lombok注解，自动生成常用方法
@Entity // JPA注解，标记为实体类
@Table(name = "notifications") // 指定表名
public class Notification {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id; // 通知ID

    @Column(nullable = false) // 非空，接收者ID
    private Long recipientId; // 接收者用户ID

    @Column(nullable = false) // 非空，通知类型
    private String type; // 通知类型（APPLICATION/ACCEPT/COMMENT）

    @Column(nullable = false, columnDefinition = "TEXT") // 非空，通知内容
    private String content; // 通知内容

    @Column(nullable = false) // 非空，关联ID
    private Long relatedId; // 关联ID（活动/申请/评论ID）

    @Column(nullable = false) // 非空，是否已读
    private Boolean isRead = false; // 是否已读

    private LocalDateTime createdAt; // 创建时间
}