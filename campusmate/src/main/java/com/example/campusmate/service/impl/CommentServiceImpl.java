package com.example.campusmate.service.impl;

import com.example.campusmate.entity.Comment;
import com.example.campusmate.repository.CommentRepository;
import com.example.campusmate.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.campusmate.repository.ActivityRepository;
import com.example.campusmate.entity.Activity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.campusmate.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.example.campusmate.Utils.UidUtils;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public Comment addComment(Comment comment) {
        // 1. 从Token获取真实用户ID
        Long realUserId = UidUtils.getUid();
        // 2. 强制将真实ID设置到comment对象中
        comment.setUserId(realUserId);
        // 3. (推荐) 校验活动是否存在
        Activity activity = activityRepository.findById(comment.getActivityId()).orElse(null);
        if (activity == null || !"PUBLISHED".equals(activity.getStatus())) {
            throw new RuntimeException("活动不存在或未发布，无法评论！");
        }
        // 4. 保存评论
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUserId(realUserId);
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByActivity(Long activityId) {
        return commentRepository.findByActivityId(activityId);
    }

    @Override
    public boolean deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return false;
        Long commentUserId = comment.getUserId();
        Long activityId = comment.getActivityId();
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) return false;
        Long postUserId = activity.getCreatorId();
        Long currentUserId = UidUtils.getUid();
        if (!currentUserId.equals(commentUserId) && !currentUserId.equals(postUserId)) {
            // 既不是评论作者也不是帖主，无权删除
            return false;
        }
        commentRepository.deleteById(commentId);
        return true;
    }

    @Override
    public Page<Comment> getCommentsByActivityPaged(Long activityId, int page, int size) {
        return commentRepository.findByActivityId(activityId, PageRequest.of(page - 1, size));
    }

    @Override
    public Page<Comment> searchComments(String keyword, int page, int size) {
        return commentRepository.findByContentContaining(keyword, PageRequest.of(page - 1, size));
    }

    @Override
    public Page<Comment> searchCommentsByActivity(Long activityId, String keyword, int page, int size) {
        return commentRepository.findByActivityIdAndContentContaining(activityId, keyword, PageRequest.of(page - 1, size));
    }
}