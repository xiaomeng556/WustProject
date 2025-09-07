package com.example.campusmate.service.impl;

import com.example.campusmate.entity.Notification;
import com.example.campusmate.repository.NotificationRepository;
import com.example.campusmate.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> listNotifications(Long userId, Integer isRead) {
        List<Notification> list = notificationRepository.findByRecipientId(userId);
        if (isRead == null) return list;
        return list.stream().filter(n -> (isRead == 1 ? Boolean.TRUE : Boolean.FALSE).equals(n.getIsRead())).toList();
    }

    @Override
    public boolean markAsRead(Long notifyId) {
        Notification n = notificationRepository.findById(notifyId).orElse(null);
        if (n == null) return false;
        n.setIsRead(true);
        notificationRepository.save(n);
        return true;
    }

    @Override
    public boolean deleteNotification(Long notifyId) {
        notificationRepository.deleteById(notifyId);
        return true;
    }

    @Override
    public Notification getNotificationById(Long notifyId) {
        return notificationRepository.findById(notifyId).orElse(null);
    }
}