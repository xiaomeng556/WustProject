package com.example.campusmate.repository;

import com.example.campusmate.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * 评论表数据库操作接口
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * 根据活动ID查询评论列表
     * @param activityId 活动ID
     * @return 评论列表
     */


    List<Comment> findByActivityId(Long activityId);
    Page<Comment> findByActivityId(Long activityId, Pageable pageable);
    Page<Comment> findByContentContaining(String keyword, Pageable pageable);
    Page<Comment> findByActivityIdAndContentContaining(Long activityId, String keyword, Pageable pageable);
}