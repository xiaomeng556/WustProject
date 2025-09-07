package com.example.campusmate.repository;

import com.example.campusmate.entity.ActivityLike;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityLikeRepository extends JpaRepository<ActivityLike, Long> {
    /**
     * 检查用户是否已点赞该活动
     */
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);
    
    /**
     * 统计活动的点赞数量
     */
    long countByActivityId(Long activityId);
    
    /**
     * 根据活动ID查询所有点赞记录
     */
    List<ActivityLike> findByActivityId(Long activityId);
    
    /**
     * 根据用户ID查询该用户的所有点赞记录
     */
    List<ActivityLike> findByUserId(Long userId);
}