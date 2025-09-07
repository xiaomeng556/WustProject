package com.example.campusmate.controller;

import com.example.campusmate.dto.ProfilePictureInformation;
import com.example.campusmate.entity.Activity;
import com.example.campusmate.entity.ActivityApplication;
import com.example.campusmate.entity.ActivityLike;
import com.example.campusmate.repository.ActivityLikeRepository;
import com.example.campusmate.service.ActivityService;
import com.example.campusmate.dto.ApplicationDetailDTO;
import com.example.campusmate.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.campusmate.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.campusmate.Utils.UidUtils;
import com.example.campusmate.repository.ActivityFavoriteRepository;
import com.example.campusmate.entity.ActivityFavorite;

/**
 * 活动控制器
 * 处理与活动相关的API请求，包括创建活动、查看活动、申请活动、喜欢/收藏活动等操作。
 */
@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
@Tag(name = "活动", description = "处理与活动相关的API请求，包括创建活动、查看活动、申请活动、喜欢/收藏活动等操作。")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ActivityFavoriteRepository activityFavoriteRepository;
    @Autowired
    private ActivityLikeRepository activityLikeRepository;
    /**
     * 创建活动
     * @param activity 创建的活动对象
     * @return ApiResponse<Activity> 返回创建后的活动信息
     */
    @Operation(summary = "创建活动", description = "创建新的活动并返回活动的详细信息。")
    @PostMapping
    public ApiResponse<Activity> createActivity(@RequestBody Activity activity) {
        Activity result = activityService.createActivity(activity);
        return ApiResponse.success(result);
    }

    /**
     * 获取活动列表（支持筛选、分页）
     * @param campus 校区筛选条件
     * @param college 学院筛选条件
     * @param type 活动类型筛选条件
     * @param page 当前页码，默认值为1
     * @param size 每页显示的活动数量，默认值为10
     * @return ApiResponse<List<Activity>> 返回活动列表
     */
    @Operation(summary = "获取活动列表", description = "获取活动列表，支持按校区、学院和类型筛选，并支持分页。")
    @GetMapping
    public ApiResponse<List<Activity>> listActivities(
            @Parameter(description = "校区筛选条件") @RequestParam(required = false) List<String> campus,
            @Parameter(description = "学院筛选条件") @RequestParam(required = false) List<String> college,
            @Parameter(description = "活动类型筛选条件") @RequestParam(required = false) List<String> type,
            @Parameter(description = "当前页码，默认值为1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页显示的活动数量，默认值为10") @RequestParam(defaultValue = "10") int size) {
        List<Activity> list = activityService.listActivities(campus, college, type, page, size);
        return ApiResponse.success(list);
    }

    /**
     * 获取活动详细信息
     * @param activityId 活动ID
     * @return ApiResponse<Activity> 返回活动的详细信息
     */
    @Operation(summary = "获取活动详细信息", description = "根据活动ID获取活动的详细信息。")
    @GetMapping("/{activityId}")
    public ApiResponse<Activity> getActivityDetail(@PathVariable Long activityId) {
        Activity activity = activityService.getActivityDetail(activityId);
        return ApiResponse.success(activity);
    }
    @Operation(summary = "获取通知里的活动详细信息", description = "根据活动ID获取活动的详细信息。")
    @GetMapping("/noti/{relatedId}")
    public ApiResponse<Activity> getActivityByRelatedId(@PathVariable Long relatedId) {
        Activity activity = activityService.getActivityByAppID(relatedId);
        return ApiResponse.success(activity);
    }

    /**
     * 根据关键字搜索活动（支持分页和筛选条件）
     * @param keyword 搜索的关键字
     * @param campus 校区筛选条件
     * @param college 学院筛选条件
     * @param type 活动类型筛选条件
     * @param page 当前页码，默认值为1
     * @param size 每页显示的活动数量，默认值为10
     * @return ApiResponse<List<Activity>> 返回搜索结果的活动列表
     */
    @Operation(summary = "搜索活动", description = "根据关键字和筛选条件搜索活动，支持分页。")
    @GetMapping("/search")
    public ApiResponse<List<Activity>> searchActivities(
            @Parameter(description = "搜索的关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "校区筛选条件") @RequestParam(required = false) List<String> campus,
            @Parameter(description = "学院筛选条件") @RequestParam(required = false) List<String> college,
            @Parameter(description = "活动类型筛选条件") @RequestParam(required = false) List<String> type,
            @Parameter(description = "当前页码，默认值为1") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页显示的活动数量，默认值为10") @RequestParam(defaultValue = "10") int size) {
        List<Activity> list = activityService.searchActivities(keyword, campus, college, type, page, size);
        return ApiResponse.success(list);
    }

    /**
     * 删除活动（需要校验操作人的权限）
     * @param activityId 活动ID
     * @return ApiResponse<Boolean> 返回删除操作的结果
     */
    @Operation(summary = "删除活动", description = "删除指定活动，仅允许创建活动的用户删除。")
    @DeleteMapping("/{activityId}")
    public ApiResponse<Boolean> deleteActivity(@PathVariable Long activityId) {
        Long uid = UidUtils.getUid();
        boolean result = activityService.deleteActivity(activityId, uid);
        if (!result) {
            return ApiResponse.fail("无权限或活动不存在");
        }
        return ApiResponse.success(true);
    }

    /**
     * 申请参加活动
     * @param activityId 活动ID
     * @param reason 申请理由
     * @return ApiResponse<Boolean> 返回申请的结果
     */
    @Operation(summary = "申请活动", description = "提交申请参加活动，并说明申请理由。")
    @PostMapping("/{activityId}/apply")
    public ApiResponse<Boolean> applyForActivity(
            @PathVariable Long activityId,
            @Parameter(description = "申请理由") @RequestParam String reason) {
        Long uid = UidUtils.getUid();
        boolean result = activityService.applyForActivity(activityId, uid, reason);
        return ApiResponse.success(result);
    }

    /**
     * 获取活动的所有申请列表
     * @param activityId 活动ID
     * @return ApiResponse<List<ApplicationDetailDTO>> 返回活动的所有申请详细信息
     */
    @Operation(summary = "获取活动申请列表", description = "获取指定活动的所有申请详细信息。")
    @GetMapping("/{activityId}/applications")
    public ApiResponse<List<ActivityApplication>> getActivityApplications(@PathVariable Long activityId) {
        List<ActivityApplication> applications = activityService.listApplications(activityId);
        applications.removeIf(
                application -> application.getStatus().equals("CANCELED")
        );
        return ApiResponse.success(applications);
    }

    @Operation(summary = "获取活动申请列表", description = "获取指定活动的所有申请详细信息。")
    @GetMapping("/{activityId}/profilePictures")
    public ApiResponse<List<ProfilePictureInformation>> getApplicationsProfilePictures(@PathVariable Long activityId) {
        List<ActivityApplication> applications = activityService.listApplications(activityId);
        applications.removeIf(
                application -> application.getStatus().equals("CANCELED")
        );
        List<ProfilePictureInformation> profiles=new ArrayList<>();
        for(ActivityApplication application:applications){
            String url= userInfoService.getUserInfo(application.getUserId()).getAvatarUrl();
            ProfilePictureInformation profilePictureInformation=new ProfilePictureInformation();
            profilePictureInformation.setProfilePicturesUrl(url);
            profilePictureInformation.setAppid(application.getId());
            profiles.add(profilePictureInformation);
        }
        return ApiResponse.success(profiles);
    }

    /**
     * 接受活动申请
     * @param appId 申请ID
     * @return ApiResponse<Boolean> 返回接受申请的结果
     */
    @Operation(summary = "接受活动申请", description = "接受指定活动的申请。")
    @PutMapping("/applications/{appId}/accept")
    public ApiResponse<Boolean> acceptApplication(@PathVariable Long appId) {
        Long uid = UidUtils.getUid();
        String result = activityService.handleApplication(appId, "ACCEPT", uid);
        if (!"SUCCESS".equals(result)) {
            return ApiResponse.fail(result);
        }
        return ApiResponse.success(true);
    }
    /**
     * 忽略活动申请
     * @param appId 申请ID
     * @return ApiResponse<Boolean> 返回忽略申请的结果
     */
    @Operation(summary = "忽略活动申请", description = "忽略指定活动的申请。")
    @PutMapping("/applications/{appId}/ignore")
    public ApiResponse<Boolean> ignoreApplication(@PathVariable Long appId) {
        Long uid = UidUtils.getUid();
        String result = activityService.handleApplication(appId, "REJECT", uid);
        if (!"SUCCESS".equals(result)) {
            return ApiResponse.fail(result);
        }
        return ApiResponse.success(true);
    }
    @Operation(summary = "忽略活动申请", description = "忽略指定活动的申请。")
    @PutMapping("/applications/{appId}/regret")
    public ApiResponse<Boolean> regret(@PathVariable Long appId) {
        Long uid = UidUtils.getUid();
        String result = activityService.handleApplication(appId, "PENDING", uid);
        if (!"SUCCESS".equals(result)) {
            return ApiResponse.fail(result);
        }
        return ApiResponse.success(true);
    }
    @Operation(summary = "取消活动申请", description = "忽略指定活动的申请。")
    @PutMapping("/applications/{appId}/cancle")
    public ApiResponse<Boolean> cancleApplication(@PathVariable Long appId) {
        Long uid = UidUtils.getUid();
        String result = activityService.handleApplication(appId, "CANCEL", uid);
        if (!"SUCCESS".equals(result)) {
            return ApiResponse.fail(result);
        }
        return ApiResponse.success(true);
    }

    /**
     * 获取用户创建的所有活动
     * @return ApiResponse<List<Activity>> 返回用户创建的活动列表
     */
    @Operation(summary = "获取用户创建的活动", description = "获取当前用户创建的所有活动。")
    @GetMapping("/user/created")
    public ApiResponse<List<Activity>> getUserCreatedActivities() {
        Long uid = UidUtils.getUid();
        List<Activity> activities = activityService.getUserCreatedActivities(uid);
        activities.removeIf(
                activity -> !(activity.getStatus().equals("PUBLISHED"))
        );
        return ApiResponse.success(activities);
    }
    /**
     * 获取用户申请的所有活动
     * @return ApiResponse<List<ActivityApplication>> 返回用户申请的活动列表
     */
    @Operation(summary = "获取用户申请的活动", description = "获取当前用户申请参加的所有活动。")
    @GetMapping("/user/applications")
    public ApiResponse<List<Activity>> getUserApplications() {
        Long uid = UidUtils.getUid();
        List<ActivityApplication> applications = activityService.getUserApplications(uid);
        List<Activity> activities=new ArrayList<>();
        for(ActivityApplication application:applications) {
            //如果是取消的 跳过
            if(application.getStatus().equals("CANCELED")) continue;
            activities.add(activityService.getActivityDetail(application.getActivityId()));
        }
        activities.removeIf(
                activity -> !(activity.getStatus().equals("PUBLISHED"))
        );
        return ApiResponse.success(activities);
    }
    /**
     * 获取用户申请的所有活动
     * @return ApiResponse<List<ActivityApplication>> 返回用户申请的活动列表
     */
    @Operation(summary = "获取用户申请的申请信息", description = "获取当前用户申请参加的所有活动的申请信息。")
    @GetMapping("/user/ApplicationsDTO")
    public ApiResponse<List<ActivityApplication>> getApplications() {
        Long uid = UidUtils.getUid();
        List<ActivityApplication> applications = activityService.getUserApplications(uid);
        applications.removeIf(application -> application.getStatus().equals("CANCELED"));
        return ApiResponse.success(applications);
    }
    @Operation(summary = "获取用户喜欢的所有活动ID", description = "获取当前用户申请参加的所有活动。")
    @GetMapping("/Activity/getAllLikedId")
    public ApiResponse<List<ActivityLike>> getAllLikedId() {
        Long uid = UidUtils.getUid();
        List<ActivityLike> applications = activityLikeRepository.findByUserId(uid);
        return ApiResponse.success(applications);
    }
    @Operation(summary = "获取用户喜欢的所有活动", description = "获取当前用户申请参加的所有活动。")
    @GetMapping("/Activity/getAllLikedActivity")
    public ApiResponse<List<Activity>> getAllLikedActivity() {
        Long uid = UidUtils.getUid();
        List<ActivityLike> applications = activityLikeRepository.findByUserId(uid);
        List<Long> ids =new ArrayList<>();
        for (ActivityLike activity:applications){
            ids.add(activity.getActivityId());
        }
        List<Activity> activities=activityService.getActivitiesByIds(ids);
        activities.removeIf(
                activity -> !(activity.getStatus().equals("PUBLISHED"))
        );
        return ApiResponse.success(activities);
    }


    /**
     * 获取用户收藏的所有活动
     * @return ApiResponse<List<Activity>> 返回用户收藏的活动列表
     */
    @Operation(summary = "获取用户收藏的活动", description = "获取当前用户收藏的所有活动。")
    @GetMapping("/user/favoritesId")
    public ApiResponse<List<ActivityFavorite>> getUserFavoriteActivitiesId() {
        Long uid = UidUtils.getUid();
        List<ActivityFavorite> activities = activityFavoriteRepository.findByUserId(uid);
        return ApiResponse.success(activities);
    }
    /**
     * 获取用户收藏的所有活动
     * @return ApiResponse<List<Activity>> 返回用户收藏的活动列表
     */
    @Operation(summary = "获取用户收藏的活动", description = "获取当前用户收藏的所有活动。")
    @GetMapping("/user/favorites")
    public ApiResponse<List<Activity>> getUserFavoriteActivities() {
        Long uid = UidUtils.getUid();
        List<ActivityFavorite> favorites = activityFavoriteRepository.findByUserId(uid);
        List<Long> activityIds = favorites.stream().map(ActivityFavorite::getActivityId).toList();
        List<Activity> activities = activityService.getActivitiesByIds(activityIds);
        activities.removeIf(
                activity -> !(activity.getStatus().equals("PUBLISHED"))
        );
        return ApiResponse.success(activities);
    }
    /**
     * 获取活动的统计数据
     * @param activityId 活动ID
     * @return ApiResponse<Map<String, Object>> 返回活动的统计数据
     */
    @Operation(summary = "获取活动统计数据", description = "获取指定活动的统计数据。")
    @GetMapping("/{activityId}/stats")
    public ApiResponse<Map<String, Object>> getActivityStats(@PathVariable Long activityId) {
        Map<String, Object> stats = activityService.getActivityStats(activityId);
        return ApiResponse.success(stats);
    }

    /**
     * 给活动点赞
     * @param activityId 活动ID
     * @return ApiResponse<Boolean> 返回点赞操作的结果
     */
    @Operation(summary = "点赞活动", description = "给指定活动点赞。")
    @PostMapping("/{activityId}/like")
    public ApiResponse<Boolean> likeActivity(@PathVariable Long activityId) {
        Long uid = UidUtils.getUid();
        boolean result = activityService.likeActivity(activityId, uid);
        return ApiResponse.success(result);
    }

    /**
     * 给活动添加收藏
     * @param activityId 活动ID
     * @return ApiResponse<Boolean> 返回收藏操作的结果
     */
    @Operation(summary = "收藏活动", description = "给指定活动添加收藏。")
    @PostMapping("/{activityId}/favorite")
    public ApiResponse<Boolean> favoriteActivity(@PathVariable Long activityId) {
        Long uid = UidUtils.getUid();
        boolean result = activityService.favoriteActivity(activityId, uid);
        return ApiResponse.success(result);
    }

    /**
     * 健康检查API
     * @return ApiResponse<String> 返回健康检查信息
     */
    @Operation(summary = "健康检查", description = "用于检查活动控制器是否工作正常。")
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ActivityController is working!");
    }

    /**
     * 调试活动数据，查看所有活动的基本信息
     * @return ApiResponse<String> 返回活动调试信息
     */
    @Operation(summary = "调试活动信息", description = "查看所有活动的基本信息，供开发和调试使用。")
    @GetMapping("/debug-activities")
    public ApiResponse<String> debugActivities() {
        List<Activity> allActivities = activityService.listActivities(null, null, null, 1, 100);
        StringBuilder result = new StringBuilder();
        result.append("数据库中的所有活动:\n");

        for (Activity activity : allActivities) {
            result.append(String.format("ID: %d, 标题: %s, 学院: '%s', 校区: '%s', 类型: '%s'\n",
                    activity.getId(),
                    activity.getTitle(),
                    activity.getCollege() != null ? activity.getCollege() : "null",
                    activity.getCampus() != null ? activity.getCampus() : "null",
                    activity.getType() != null ? activity.getType() : "null"));
        }

        return ApiResponse.success(result.toString());
    }
}
