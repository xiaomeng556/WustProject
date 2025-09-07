package com.example.campusmate.repository;

import com.example.campusmate.entity.ActivityFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityFavoriteRepository extends JpaRepository<ActivityFavorite, Long> {
    /**
     * 检查用户是否已收藏该活动
     */
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);
    
    /**
     * 统计活动的收藏数量
     */
    long countByActivityId(Long activityId);
    
    /**
     * 根据活动ID查询所有收藏记录
     */
    List<ActivityFavorite> findByActivityId(Long activityId);
    
    /**
     * 根据用户ID查询该用户的所有收藏记录
     */
    List<ActivityFavorite> findByUserId(Long userId);
}