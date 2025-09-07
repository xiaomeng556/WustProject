package com.example.campusmate.repository;

import com.example.campusmate.entity.ActivityApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ActivityApplicationRepository extends JpaRepository<ActivityApplication, Long> {
    /**
     * 检查用户是否已经申请过该活动
     */
    boolean existsByActivityIdAndUserId(Long activityId, Long userId);
    
    /**
     * 根据活动ID查询所有申请
     */
    List<ActivityApplication> findByActivityId(Long activityId);
    
    /**
     * 根据活动ID和状态查询申请
     */
    List<ActivityApplication> findByActivityIdAndStatus(Long activityId, String status);
    List<ActivityApplication> findByActivityIdAndUserId(Long activityId, Long userId);

    /**
     * 根据用户ID查询该用户的所有申请
     */
    List<ActivityApplication> findByUserId(Long userId);
    
    /**
     * 根据用户ID和状态查询申请
     */
    List<ActivityApplication> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * 统计活动的申请数量
     */
    long countByActivityId(Long activityId);
    
    /**
     * 统计活动的待处理申请数量
     */
    long countByActivityIdAndStatus(Long activityId, String status);
}