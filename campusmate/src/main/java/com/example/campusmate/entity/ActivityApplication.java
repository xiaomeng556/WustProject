package com.example.campusmate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_applications")
public class ActivityApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long activityId;

    @Column(name = "applicant_id", nullable = false)
    private Long applicantId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String contactInfo;

    // 关键：设置userId时自动同步给applicantId
    public void setUserId(Long userId) {
        this.userId = userId;
        this.applicantId = userId; // applicant_id随user_id自动变化
    }

    @PrePersist
    protected void onCreate() {
        // 只在字段为null时才自动设置，保留手动设置的值
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
