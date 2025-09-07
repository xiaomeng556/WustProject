package com.example.campusmate.controller;

import com.example.campusmate.entity.Notification;
import com.example.campusmate.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.campusmate.dto.ApiResponse;
import com.example.campusmate.Utils.UidUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 消息通知控制器
 * 处理与消息通知相关的API请求，包括获取通知列表、查看通知详情、标记通知为已读、删除通知等操作。
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@Tag(name = "消息通知", description = "处理与消息通知相关的API请求，包括获取通知列表、查看通知详情、标记通知为已读、删除通知等操作。")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 获取当前用户的通知列表
     * @param isRead 是否已读（可选参数，若传入值则筛选已读或未读通知）
     * @return ApiResponse<List<Notification>> 返回当前用户的通知列表
     */
    @Operation(summary = "获取通知列表", description = "获取当前用户的通知列表，并根据 isRead 筛选通知。")
    @GetMapping
    public ApiResponse<List<Notification>> listNotifications(
            @Parameter(description = "是否已读，传入值则筛选已读或未读通知") @RequestParam(required = false) Integer isRead) {
        // 获取当前登录用户的UID
        Long uid = UidUtils.getUid();

        // 获取通知列表
        List<Notification> list = notificationService.listNotifications(uid, isRead);

//        // 自动将所有未读消息标记为已读
//        for (Notification n : list) {
//            if (!Boolean.TRUE.equals(n.getIsRead())) {
//                notificationService.markAsRead(n.getId()); // 标记为已读
//            }
//        }

        return ApiResponse.success(list); // 返回通知列表
    }

    /**
     * 查看单个通知的详细信息
     * @param notifyId 通知的ID
     * @return ApiResponse<Notification> 返回通知的详细信息
     */
    @Operation(summary = "查看通知详情", description = "查看单个通知的详细信息，并标记为已读。")
    @GetMapping("/{notifyId}")
    public ApiResponse<Notification> getNotificationDetail(
            @Parameter(description = "通知ID") @PathVariable Long notifyId) {
        // 获取当前登录用户的UID
        Long uid = UidUtils.getUid();
        // 根据通知ID获取通知
        Notification notification = notificationService.getNotificationById(notifyId);
        // 判断通知是否存在，且是否属于当前用户
        if (notification == null || !notification.getRecipientId().equals(uid)) {
            return ApiResponse.fail("无权限查看该消息"); // 如果没有权限查看，返回错误
        }

        // 如果通知为未读，标记为已读
        if (!Boolean.TRUE.equals(notification.getIsRead())) {
            notificationService.markAsRead(notifyId);
            notification.setIsRead(true); // 立即反映到返回值
        }

        return ApiResponse.success(notification); // 返回通知详细信息
    }

    /**
     * 标记通知为已读
     * @param notifyId 通知的ID
     * @return ApiResponse<Boolean> 返回操作结果
     */
    @Operation(summary = "标记通知为已读", description = "将指定的通知标记为已读。")
    @PutMapping("/{notifyId}/read")
    public ApiResponse<Boolean> markAsRead(
            @Parameter(description = "通知ID") @PathVariable Long notifyId) {
        // 调用服务层方法标记通知为已读
        boolean result = notificationService.markAsRead(notifyId);

        return ApiResponse.success(result); // 返回操作结果
    }

    /**
     * 删除指定通知
     * @param notifyId 通知的ID
     * @return ApiResponse<Boolean> 返回操作结果
     */
    @Operation(summary = "删除通知", description = "删除指定的通知。")
    @DeleteMapping("/{notifyId}")
    public ApiResponse<Boolean> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long notifyId) {
        // 获取当前登录用户的UID
        Long uid = UidUtils.getUid();

        // 根据通知ID获取通知
        Notification notification = notificationService.getNotificationById(notifyId);

        // 判断通知是否存在，且是否属于当前用户
        if (notification == null || !notification.getRecipientId().equals(uid)) {
            return ApiResponse.fail("无权限删除该通知"); // 如果没有权限删除，返回错误
        }

        // 调用服务层方法删除通知
        boolean result = notificationService.deleteNotification(notifyId);

        return ApiResponse.success(result); // 返回操作结果
    }
}
