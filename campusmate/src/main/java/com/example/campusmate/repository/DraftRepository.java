package com.example.campusmate.repository;

import com.example.campusmate.entity.Draft;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 草稿箱表数据库操作接口
 */
public interface DraftRepository extends JpaRepository<Draft, Long> {
    /**
     * 根据用户ID查询草稿列表
     * @param userId 用户ID
     * @return 草稿列表
     */
    List<Draft> findByUserId(Long userId);
    
    /**
     * 根据活动ID查询草稿
     * @param activityId 活动ID
     * @return 草稿信息
     */
    Draft findByActivityId(Long activityId);
    
    /**
     * 根据用户ID和原因查询草稿
     * @param userId 用户ID
     * @param reason 原因
     * @return 草稿列表
     */
    List<Draft> findByUserIdAndReason(Long userId, String reason);
    
    /**
     * 统计用户的草稿数量
     * @param userId 用户ID
     * @return 草稿数量
     */
    long countByUserId(Long userId);
}