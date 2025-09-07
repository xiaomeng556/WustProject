package com.example.campusmate.service.impl;

import com.example.campusmate.entity.Activity;
import com.example.campusmate.repository.ActivityRepository;
import com.example.campusmate.repository.UserInfoRepository;
import com.example.campusmate.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.campusmate.entity.UserInfo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.campusmate.dto.ApplicationDetailDTO;
import com.example.campusmate.entity.ActivityApplication;
import com.example.campusmate.repository.DraftRepository;
import com.example.campusmate.entity.Draft;
import com.example.campusmate.repository.ActivityApplicationRepository;
import com.example.campusmate.Utils.UidUtils;
import com.example.campusmate.repository.ActivityLikeRepository;
import com.example.campusmate.repository.ActivityFavoriteRepository;
import com.example.campusmate.repository.NotificationRepository;
import com.example.campusmate.entity.ActivityLike;
import com.example.campusmate.entity.ActivityFavorite;
import com.example.campusmate.entity.Notification;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityApplicationRepository activityApplicationRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private DraftRepository draftRepository;
    @Autowired
    private ActivityLikeRepository activityLikeRepository;
    @Autowired
    private ActivityFavoriteRepository activityFavoriteRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Activity createActivity(Activity activity) {
        Long uid = UidUtils.getUid();
        activity.setCreatorId(uid);

        activity.setCreatedAt(LocalDateTime.now());
        activity.setStatus("PUBLISHED");//PUBLISHED
        return activityRepository.save(activity);
    }

    @Override
    public List<Activity> listActivities(List<String> campuses, List<String> colleges, List<String> types, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        List<String> campusFilter = (campuses != null && !campuses.isEmpty()) ? campuses : null;
        List<String> collegeFilter = (colleges != null && !colleges.isEmpty()) ? colleges : null;
        List<String> typeFilter = (types != null && !types.isEmpty()) ? types : null;
        return activityRepository.findActivitiesWithFilters(
                campusFilter, collegeFilter, typeFilter, "PUBLISHED", pageable)
                .getContent();
    }

    @Override
    public Activity getActivityDetail(Long activityId) {

        return activityRepository.findById(activityId).orElse(null);
    }

    @Override
    public boolean deleteActivity(Long activityId, Long userId) {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) return false;
        if (!activity.getCreatorId().equals(userId)) {
            // 非本人不能删除
            return false;
        }
//        // 转存为草稿，拷贝所有内容字段
//        Draft draft = new Draft();
//        draft.setUserId(userId);
//        draft.setActivityId(activityId);
//        draft.setReason("DELETED");
//        draft.setCreatedAt(LocalDateTime.now());
//        draft.setTitle(activity.getTitle());
//        draft.setDescription(activity.getDescription());
//        draft.setType(activity.getType());
//        draft.setActivityTime(activity.getActivityTime());
//        draft.setLocation(activity.getLocation());
//        draft.setMinPeople(activity.getMinPeople());
//        draft.setMaxPeople(activity.getMaxPeople());
//        draft.setExpireTime(activity.getExpireTime());
//        draft.setCampus(activity.getCampus());
//        draft.setCollege(activity.getCollege());
//        draft.setTags(activity.getTags());
//        draft.setStatus("DRAFT"); // 强制设为DRAFT
//        draft.setUpdatedAt(activity.getUpdatedAt());
//        draft.setImageUrl(activity.getImageUrl());
//        draftRepository.save(draft);
        activity.setStatus("DRAFT");
        activity.setUpdatedAt(LocalDateTime.now());
        activityRepository.save(activity);
        return true;
    }

    @Override
    public boolean applyForActivity(Long activityId, Long userId, String reason) {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            return false;
        }
        // 校验：不能申请自己发布的项目
        if (activity.getCreatorId().equals(userId)) {
            return false;
        }
        // 防止重复申请
        if (activityApplicationRepository.existsByActivityIdAndUserId(activityId, userId)) {
            List<ActivityApplication> applications = activityApplicationRepository.findByActivityIdAndUserId(activityId, userId);
            boolean signal = false;
            //若为取消过的 则不适为重复申请
            for (ActivityApplication application : applications) {
                if (!application.getStatus().equals("CANCELED")) {
                    signal = true;
                }
            }
            if (signal) {
                return false;
            }
        }
        ActivityApplication application = new ActivityApplication();
        application.setActivityId(activityId);
        application.setUserId(userId);
        application.setReason(reason);
        application.setStatus("PENDING");
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.parse("2024-01-15T12:28:59"));
        activityApplicationRepository.save(application);
        // 通知活动创建者
            Notification notification = new Notification();
            notification.setRecipientId(activity.getCreatorId());
            notification.setType("活动申请");
            notification.setContent("有新的活动申请！");
            notification.setRelatedId(application.getId());
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        return true;
    }

    @Override
    public List<ActivityApplication> listApplications(Long activityId) {
        List<ActivityApplication> applications = activityApplicationRepository.findByActivityId(activityId);
//        Activity activity = activityRepository.findById(activityId).orElse(null);
//        List<ApplicationDetailDTO> result = new java.util.ArrayList<>();
//        for (ActivityApplication app : applications) {
//            ApplicationDetailDTO dto = new ApplicationDetailDTO();
//            dto.setId(app.getId());
//            dto.setActivityId(app.getActivityId());
//            dto.setUserId(app.getUserId());
//            dto.setReason(app.getReason());
//            dto.setStatus(app.getStatus());
//            dto.setCreatedAt(app.getCreatedAt());
//            dto.setUpdatedAt(app.getUpdatedAt());
//            UserInfo user = userInfoRepository.findByUserId(app.getUserId());
//            if (user != null) {
//                dto.setStudentId(user.getStudentId());
//                dto.setCollege(user.getCollege());
//                dto.setCampus(user.getCampus());
//                dto.setMajor(user.getMajor());
//                dto.setGrade(user.getGrade());
//                dto.setSignature(user.getSignature());
//                dto.setInterests(user.getInterests());
//                dto.setSkills(user.getSkills());
//                dto.setAvatarUrl(user.getAvatarUrl());
//            }
//            if (activity != null) {
//                dto.setActivityTitle(activity.getTitle());
//                dto.setActivityType(activity.getType());
//                dto.setActivityTime(activity.getActivityTime());
//                dto.setActivityLocation(activity.getLocation());
//            }
//            result.add(dto);
//        }
        return applications;
    }

    @Override
    public String handleApplication(Long appId, String action, Long userId) {
        ActivityApplication app = activityApplicationRepository.findById(appId).orElse(null);
        if (app == null) {
            return null;
        }
        if(action.equals("PENDING")){
            if (action.equals(app.getStatus())) {
                return "该申请已被处理";
            }
        }else{
            if ((action+"ED").equals(app.getStatus())) {
                return "该申请已被处理";
            }
        }
        Activity activity = activityRepository.findById(app.getActivityId()).orElse(null);
        if (activity == null) {
            return "查询不到活动？";
        }
        if (!activity.getCreatorId().equals(userId)&&!("CANCEL".equals(action))) {
            return "您无权限进行该操作";
        }
        if ("ACCEPT".equals(action)) {
            app.setStatus("ACCEPTED");
        } else if ("REJECT".equals(action)) {
            app.setStatus("REJECTED");
        } else if ("CANCEL".equals(action)){
            app.setStatus("CANCELED");
        } else if ("PENDING".equals(action)){
            List<Notification> notifications= notificationRepository.findByRelatedId(appId);
            //删除已发的通知
            for(Notification notification1:notifications){
                if(notification1.getType().equals("ACCEPT")){
                    notificationRepository.delete(notification1);
                }
            }

            app.setStatus("PENDING");
        }else{
            return "未知的操作";
        }
        app.setUpdatedAt(LocalDateTime.now());
        activityApplicationRepository.save(app);
        // 只在同意时返回联系方式
        if ("ACCEPT".equals(action)) {
            UserInfo creator = userInfoRepository.findByUserId(userId);
            if (creator == null) {
                return "找不到用户信息";
            }
            String contactVisibility = creator.getContactVisibility();
            StringBuilder contactInfo;
            if (contactVisibility.equals("PUBLIC")){
                contactInfo = new StringBuilder("QQ:"+creator.getQq()+" 微信号:"+creator.getWechat()+"电话号码："+creator.getPhone());
            }else{
                contactInfo = new StringBuilder("QQ:"+creator.getQq()+" 微信号:"+creator.getWechat()+"电话号码："+creator.getPhone());
            }
            // 发送通知给申请人
            Notification notification = new Notification();
            notification.setRecipientId(app.getUserId()); // 申请人
            notification.setType("ACCEPT");
            notification.setContent("联系方式如下：" + contactInfo);
            notification.setRelatedId(appId);
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            return "SUCCESS";
        }
        return "SUCCESS";
    }
    //点赞活动
    @Override
    public boolean likeActivity(Long activityId, Long userId) {
        if (activityLikeRepository.existsByActivityIdAndUserId(activityId, userId)) {
            // 已点赞则取消点赞
            List<ActivityLike> likes = activityLikeRepository.findByActivityId(activityId);
            for (ActivityLike like : likes) {
                if (like.getUserId().equals(userId)) {
                    activityLikeRepository.delete(like);
                    return false;
                }
            }
        } else {
            ActivityLike like = new ActivityLike();
            like.setActivityId(activityId);
            like.setUserId(userId);
            like.setCreatedAt(LocalDateTime.now());
            activityLikeRepository.save(like);
            return true;
        }
        return true;
    }
    //收藏
    @Override
    public boolean favoriteActivity(Long activityId, Long userId) {
        if (activityFavoriteRepository.existsByActivityIdAndUserId(activityId, userId)) {
            // 已收藏则取消收藏
            List<ActivityFavorite> favorites = activityFavoriteRepository.findByActivityId(activityId);
            for (ActivityFavorite fav : favorites) {
                if (fav.getUserId().equals(userId)) {
                    activityFavoriteRepository.delete(fav);
                    return false;
                }
            }
        } else {
            ActivityFavorite favorite = new ActivityFavorite();
            favorite.setActivityId(activityId);
            favorite.setUserId(userId);
            favorite.setCreatedAt(LocalDateTime.now());
            activityFavoriteRepository.save(favorite);
            return true;
        }
        return true;
    }

    @Override
    public List<Activity> getUserCreatedActivities(Long userId) {
        return activityRepository.findByCreatorId(userId);
    }

    @Override
    public List<ActivityApplication> getUserApplications(Long userId) {
        return activityApplicationRepository.findByUserId(userId);
    }

    @Override
    public long getActivityApplicationCount(Long activityId) {
        return activityApplicationRepository.countByActivityId(activityId);
    }

    @Override
    public long getPendingApplicationCount(Long activityId) {
        return activityApplicationRepository.countByActivityIdAndStatus(activityId, "PENDING");
    }

    @Override
    public Map<String, Object> getActivityStats(Long activityId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("applicationCount", activityApplicationRepository.countByActivityId(activityId));
        stats.put("pendingCount", activityApplicationRepository.countByActivityIdAndStatus(activityId, "PENDING"));
        stats.put("likeCount", activityLikeRepository.countByActivityId(activityId));
        stats.put("favoriteCount", activityFavoriteRepository.countByActivityId(activityId));
        return stats;
    }

    @Override
    public void handleExpiredActivities() {
        List<Activity> expired = activityRepository.findExpiredActivities(LocalDateTime.now());
        for (Activity activity : expired) {
            activity.setStatus("EXPIRED");
            activity.setUpdatedAt(LocalDateTime.now());
            activityRepository.save(activity);
            // 转存为草稿，拷贝所有内容字段
            Draft draft = new Draft();
            draft.setUserId(activity.getCreatorId());
            draft.setActivityId(activity.getId());
            draft.setReason("EXPIRED");
            draft.setCreatedAt(LocalDateTime.now());
            draft.setTitle(activity.getTitle());
            draft.setDescription(activity.getDescription());
            draft.setType(activity.getType());
            draft.setActivityTime(activity.getActivityTime());
            draft.setLocation(activity.getLocation());
            draft.setMinPeople(activity.getMinPeople());
            draft.setMaxPeople(activity.getMaxPeople());
            draft.setExpireTime(activity.getExpireTime());
            draft.setCampus(activity.getCampus());
            draft.setCollege(activity.getCollege());
            draft.setTags(activity.getTags());
            draft.setStatus("DRAFT"); // 强制设为DRAFT
            draft.setUpdatedAt(activity.getUpdatedAt());
            draft.setImageUrl(activity.getImageUrl());
            draftRepository.save(draft);
        }
    }

    @Override
    public boolean isActivityExpired(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) return false;
        return activity.getExpireTime().isBefore(LocalDateTime.now()) || "EXPIRED".equals(activity.getStatus());
    }

    @Override
    public List<Activity> searchActivities(String keyword, List<String> campuses, List<String> colleges, List<String> types, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return activityRepository.searchByKeyword(
                (keyword == null || keyword.isEmpty()) ? null : keyword,
                (campuses != null && !campuses.isEmpty()) ? campuses : null,
                (colleges != null && !colleges.isEmpty()) ? colleges : null,
                (types != null && !types.isEmpty()) ? types : null,
                "PUBLISHED",
                pageable
        ).getContent();
    }

    @Override
    public List<Activity> getActivitiesByIds(List<Long> activityIds) {
        if (activityIds == null || activityIds.isEmpty()) return java.util.Collections.emptyList();
        return activityRepository.findAllById(activityIds);
    }

    @Override
    public Activity getActivityByAppID(Long appId) {
        ActivityApplication application=activityApplicationRepository.getReferenceById(appId);
        return activityRepository.findById(application.getActivityId()).orElse(null);
    }
}