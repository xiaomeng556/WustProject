package com.example.campusmate.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "activity_favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"activityId", "userId"}))
public class ActivityFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long activityId;
    private Long userId;
    private LocalDateTime createdAt;
}