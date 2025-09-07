package com.example.campusmate.service;

import com.example.campusmate.entity.Notification;
import java.util.List;

/**
 * 通知相关业务接口
 */
public interface NotificationService {
    /**
     * 获取用户通知列表
     * @param userId 用户ID
     * @param isRead 是否已读
     * @return 通知列表
     */
    List<Notification> listNotifications(Long userId, Integer isRead);

    /**
     * 标记通知为已读
     * @param notifyId 通知ID
     * @return 是否成功
     */
    boolean markAsRead(Long notifyId);

    /**
     * 删除通知
     * @param notifyId 通知ID
     * @return 是否成功
     */
    boolean deleteNotification(Long notifyId);

    Notification getNotificationById(Long notifyId);
}