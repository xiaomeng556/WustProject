package com.example.campusmate.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 申请详情DTO
 * 包含申请信息和申请人基本信息
 */
@Data
public class ApplicationDetailDTO {
    private Long id; // 申请ID
    private Long activityId; // 活动ID
    private Long userId; // 申请人ID
    private String reason; // 申请理由
    private String status; // 申请状态
    private LocalDateTime createdAt; // 申请时间
    private LocalDateTime updatedAt; // 状态更新时间
    
    // 申请人信息
    private String studentId; // 学号
    private String college; // 学院
    private String campus; // 校区
    private String major; // 专业
    private String grade; // 年级
    private String signature; // 个性签名
    private String interests; // 兴趣爱好
    private String skills; // 技能特长
    private String avatarUrl; // 头像URL
    
    // 活动信息
    private String activityTitle; // 活动标题
    private String activityType; // 活动类型
    private LocalDateTime activityTime; // 活动时间
    private String activityLocation; // 活动地点
} 