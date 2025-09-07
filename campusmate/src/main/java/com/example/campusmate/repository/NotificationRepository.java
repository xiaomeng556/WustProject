package com.example.campusmate.repository;

import com.example.campusmate.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 通知表数据库操作接口
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * 根据接收者ID查询通知列表
     * @param recipientId 接收者用户ID
     * @return 通知列表
     */
    List<Notification> findByRecipientId(Long recipientId);
    List<Notification> findByRelatedId(Long relatedId);

}