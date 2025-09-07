package com.example.campusmate.config;

import com.example.campusmate.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置类
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    
    @Autowired
    private ActivityService activityService;
    
    /**
     * 每天凌晨2点自动处理过期活动
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void handleExpiredActivities() {
        System.out.println("开始处理过期活动...");
        activityService.handleExpiredActivities();
    }
    
    /**
     * 每小时检查一次即将过期的活动
     */
    @Scheduled(fixedRate = 3600000) // 1小时 = 3600000毫秒
    public void checkUpcomingExpiredActivities() {
        System.out.println("检查即将过期的活动...");
        // 可以在这里添加通知逻辑
    }
} 