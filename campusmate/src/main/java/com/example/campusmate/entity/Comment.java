package com.example.campusmate.entity;

import jakarta.persistence.*; // JPA注解包
import lombok.Data; // Lombok自动生成getter/setter等
import java.time.LocalDateTime; // Java8时间类

@Data // Lombok注解，自动生成常用方法
@Entity // JPA注解，标记为实体类
@Table(name = "comments") // 指定表名
public class Comment {
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id; // 评论ID

    @Column(nullable = false) // 非空，活动ID
    private Long activityId; // 关联活动ID

    @Column(nullable = false) // 非空，评论用户ID
    private Long userId; // 评论用户ID

    @Column(nullable = false, columnDefinition = "TEXT") // 非空，评论内容
    private String content; // 评论内容

    private Long parentId; // 父评论ID（用于回复）

    private LocalDateTime createdAt; // 创建时间
}