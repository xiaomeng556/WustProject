package com.example.campusmate.controller;

import com.example.campusmate.entity.Comment;
import com.example.campusmate.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.campusmate.dto.ApiResponse;
import org.springframework.data.domain.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 评论控制器
 * 处理与评论相关的API请求，包括添加评论、获取评论、删除评论等操作。
 */
@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
@Tag(name = "评论", description = "处理与评论相关的API请求，包括添加评论、获取评论、删除评论等操作。")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     * @param comment 要添加的评论对象
     * @return ApiResponse<Comment> 返回添加后的评论对象
     */
    @Operation(summary = "添加评论", description = "添加一个新的评论。")
    @PostMapping
    public ApiResponse<Comment> addComment(@RequestBody Comment comment) {
        // 调用服务层方法添加评论
        Comment result = commentService.addComment(comment);
        // 返回成功响应，包含新增的评论信息
        return ApiResponse.success(result);
    }

    /**
     * 根据活动ID分页获取评论
     * @param activityId 活动ID
     * @param page 当前页码，默认值为1
     * @param size 每页显示的评论数量，默认值为10
     * @return ApiResponse<Page<Comment>> 返回该活动的评论分页列表
     */
    @Operation(summary = "获取活动评论", description = "根据活动ID分页获取该活动的评论。")
    @GetMapping("/activity/{activityId}")
    public ApiResponse<Page<Comment>> getCommentsByActivityPaged(
            @Parameter(description = "活动ID") @PathVariable Long activityId,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页显示的评论数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        // 调用服务层方法根据活动ID分页获取评论

        Page<Comment> result = commentService.getCommentsByActivityPaged(activityId, page, size);
        // 返回评论分页结果
        return ApiResponse.success(result);
    }

    @Operation(summary = "获取所有活动评论", description = "根据活动ID分页获取该活动的评论。")
    @GetMapping("/activity/getAllComments/{activityId}")
    public ApiResponse<List<Comment>> getCommentsByActivity(
            @Parameter(description = "活动ID") @PathVariable Long activityId) {
        // 调用服务层方法根据活动ID分页获取评论
        List<Comment> result = commentService.getCommentsByActivity(activityId);
        // 返回评论分页结果
        return ApiResponse.success(result);
    }

    /**
     * 根据关键词搜索评论
     * @param keyword 搜索的关键词
     * @param page 当前页码，默认值为1
     * @param size 每页显示的评论数量，默认值为10
     * @return ApiResponse<Page<Comment>> 返回根据关键词搜索到的评论分页列表
     */
    @Operation(summary = "搜索评论", description = "根据关键词搜索评论并分页返回结果。")
    @GetMapping("/search")
    public ApiResponse<Page<Comment>> searchComments(
            @Parameter(description = "搜索的关键词") @RequestParam String keyword,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页显示的评论数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        // 调用服务层方法根据关键词搜索评论
        Page<Comment> result = commentService.searchComments(keyword, page, size);
        // 返回评论分页结果
        return ApiResponse.success(result);
    }

    /**
     * 根据活动ID和关键词搜索评论
     * @param activityId 活动ID
     * @param keyword 搜索的关键词
     * @param page 当前页码，默认值为1
     * @param size 每页显示的评论数量，默认值为10
     * @return ApiResponse<Page<Comment>> 返回该活动中根据关键词搜索到的评论分页列表
     */
    @Operation(summary = "根据活动ID和关键词搜索评论", description = "根据活动ID和关键词搜索该活动中的评论并分页返回结果。")
    @GetMapping("/activity/{activityId}/search")
    public ApiResponse<Page<Comment>> searchCommentsByActivity(
            @Parameter(description = "活动ID") @PathVariable Long activityId,
            @Parameter(description = "搜索的关键词") @RequestParam String keyword,
            @Parameter(description = "当前页码", example = "1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页显示的评论数量", example = "10") @RequestParam(defaultValue = "10") int size) {
        // 调用服务层方法根据活动ID和关键词搜索评论
        Page<Comment> result = commentService.searchCommentsByActivity(activityId, keyword, page, size);
        // 返回评论分页结果
        return ApiResponse.success(result);
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return ApiResponse<Boolean> 返回删除操作的结果，true表示成功，false表示失败
     */
    @Operation(summary = "删除评论", description = "根据评论ID删除评论。")
    @DeleteMapping("/{commentId}")
    public ApiResponse<Boolean> deleteComment(
            @Parameter(description = "评论ID") @PathVariable Long commentId) {
        // 调用服务层方法删除评论
        boolean result = commentService.deleteComment(commentId);
        // 返回删除操作的结果
        return ApiResponse.success(result);
    }
}
