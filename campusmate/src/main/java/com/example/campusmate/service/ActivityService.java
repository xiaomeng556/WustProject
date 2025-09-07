package com.example.campusmate.service;

import com.example.campusmate.entity.Activity;
import com.example.campusmate.entity.ActivityApplication;
import com.example.campusmate.dto.ApplicationDetailDTO;
import java.util.List;
import java.util.Map;

/**
 * 活动管理相关业务接口
 */
public interface ActivityService {
    /**
     * 创建活动
     * @param activity 活动信息
     * @return 创建后的活动
     */
    Activity createActivity(Activity activity);

    /**
     * 获取活动列表（带筛选）
     * @param campuses 校区列表
     * @param colleges 学院列表
     * @param types 活动类型列表
     * @param page 页码
     * @param size 每页数量
     * @return 活动列表
     */
    List<Activity> listActivities(List<String> campuses, List<String> colleges, List<String> types, int page, int size);

    /**
     * 获取活动详情
     * @param activityId 活动ID
     * @return 活动详情
     */
    Activity getActivityDetail(Long activityId);

    /**
     * 删除活动（校验操作人身份）
     * @param activityId 活动ID
     * @param userId 操作人ID
     * @return 是否成功
     */
    boolean deleteActivity(Long activityId, Long userId);

    /**
     * 申请成为搭子
     * @param activityId 活动ID
     * @param userId 申请人ID
     * @param reason 申请理由
     * @return 是否成功
     */
    boolean applyForActivity(Long activityId, Long userId, String reason);

    /**
     * 获取活动的申请列表
     * @param activityId 活动ID
     * @return 申请详情列表
     */
    List<ActivityApplication> listApplications(Long activityId);

    /**
     * 处理申请（同意/拒绝）
     * @param appId 申请ID
     * @param action 操作（ACCEPT/REJECT）
     * @return 是否成功
     */
    String handleApplication(Long appId, String action, Long userId);

    /**
     * 点赞活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean likeActivity(Long activityId, Long userId);

    /**
     * 收藏活动
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean favoriteActivity(Long activityId, Long userId);

    /**
     * 获取用户创建的活动列表
     * @param userId 用户ID
     * @return 活动列表
     */
    List<Activity> getUserCreatedActivities(Long userId);

    /**
     * 获取用户申请的活动列表
     * @param userId 用户ID
     * @return 申请列表
     */
    List<ActivityApplication> getUserApplications(Long userId);

    /**
     * 获取活动的申请数量
     * @param activityId 活动ID
     * @return 申请数量
     */
    long getActivityApplicationCount(Long activityId);

    /**
     * 获取活动的待处理申请数量
     * @param activityId 活动ID
     * @return 待处理申请数量
     */
    long getPendingApplicationCount(Long activityId);

    /**
     * 获取活动统计信息
     * @param activityId 活动ID
     * @return 统计信息
     */
    Map<String, Object> getActivityStats(Long activityId);

    /**
     * 自动处理过期活动
     */
    void handleExpiredActivities();

    /**
     * 检查活动是否已过期
     * @param activityId 活动ID
     * @return 是否过期
     */
    boolean isActivityExpired(Long activityId);

    /**
     * 关键字模糊搜索活动（支持分页、筛选）
     * @param keyword 关键字
     * @param campuses 校区列表
     * @param colleges 学院列表
     * @param types 类型列表
     * @param page 页码
     * @param size 每页数量
     * @return 活动列表
     */
    List<Activity> searchActivities(String keyword, List<String> campuses, List<String> colleges, List<String> types, int page, int size);

    /**
     * 根据活动ID列表获取活动列表
     * @param activityIds 活动ID列表
     * @return 活动列表
     */
    List<Activity> getActivitiesByIds(List<Long> activityIds);

     Activity getActivityByAppID(Long appId);
}