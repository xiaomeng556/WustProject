package com.example.campusmate.service;

import com.example.campusmate.entity.Comment;
import org.springframework.data.domain.Page;
import java.util.List;

/**
 * 评论相关业务接口
 */
public interface CommentService {
    /**
     * 发布评论
     * @param comment 评论内容
     * @return 新增评论
     */
    Comment addComment(Comment comment);

    /**
     * 获取活动评论列表
     * @param activityId 活动ID
     * @return 评论列表
     */
    List<Comment> getCommentsByActivity(Long activityId);

    /**
     * 删除评论（仅作者可操作）
     * @param commentId 评论ID
     * @return 是否成功
     */
    boolean deleteComment(Long commentId);

    Page<Comment> getCommentsByActivityPaged(Long activityId, int page, int size);
    Page<Comment> searchComments(String keyword, int page, int size);
    Page<Comment> searchCommentsByActivity(Long activityId, String keyword, int page, int size);
}