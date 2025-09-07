package com.example.campusmate.controller;

import com.example.campusmate.Utils.UidUtils;
import com.example.campusmate.entity.UserInfo;
import com.example.campusmate.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.campusmate.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 用户信息控制器
 * 处理用户信息相关的API请求，包括查询当前用户信息、更新用户信息、设置信息可见性等
 * 映射路径：/api/user
 * 支持跨域请求（允许所有来源）
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@Tag(name = "用户信息", description = "处理用户信息管理，包括获取和更新个人信息、可见性设置等")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 获取当前登录用户的个人信息
     * @return ApiResponse<UserInfo> 包含当前用户信息的成功响应
     */
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的个人信息。")
    @GetMapping("/me")
    public ApiResponse<UserInfo> getCurrentUserInfo() {
        //获取用户学号
        Long uid = UidUtils.getUid();
        //根据uid获取个人信息 未注册自动注册
        UserInfo userInfo = userInfoService.getUserInfo(uid);

        return ApiResponse.success(userInfo);
    }


    /**
     * 更新当前登录用户的个人信息
     * @param userInfo 包含待更新信息的UserInfo对象
     * @return ApiResponse<UserInfo> 包含更新后的用户信息的响应
     */
    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的个人信息。")
    @PutMapping("/me")
    public ApiResponse<UserInfo> updateCurrentUserInfo(@RequestBody UserInfo userInfo) {
        Long uid = UidUtils.getUid();
        System.out.println(userInfo.getInfoVisibility());
        System.out.println(userInfo.getContactVisibility());
        //先获取 没有自动创建
        boolean result = userInfoService.updateUserInfo(uid, userInfo);
        if (!result) {
            return ApiResponse.fail("更新失败，联系方式至少有一个不能为空");
        }
        UserInfo updatedUserInfo =userInfoService.getUserInfo(uid);
        //返回更新成功后的后台数据
        return ApiResponse.success(updatedUserInfo);
    }
    /**
     * 更新当前登录用户的信息可见性设置
     * @param visibility 可见性配置字符串
     * @return ApiResponse<Boolean> 包含更新结果的响应
     */
    @Operation(summary = "更新当前用户可见性设置", description = "更新当前登录用户的个人信息可见性设置。")
    @PutMapping("/me/settings")
    public ApiResponse<Boolean> updateCurrentUserVisibility(@RequestBody String visibility) {
        Long uid = UidUtils.getUid();
        boolean result = userInfoService.updateVisibility(uid, visibility);
        return ApiResponse.success(result);
    }

    /**
     * 更新当前登录用户的联系方式信息
     * @param contactInfo 包含联系方式信息的UserInfo对象
     * @return ApiResponse<Boolean> 包含更新结果的响应
     */
    @Operation(summary = "更新联系方式信息", description = "更新当前登录用户的联系方式（QQ、微信、手机号）。")
    @PutMapping("/me/contact")
    public ApiResponse<Boolean> updateContactInfo(@RequestBody UserInfo contactInfo) {
        Long uid = UidUtils.getUid();
        boolean result = userInfoService.updateContactInfo(
                uid,
                contactInfo.getQq(),
                contactInfo.getWechat(),
                contactInfo.getPhone(),
                contactInfo.getContactVisibility()
        );
        return ApiResponse.success(result);
    }

    /**
     * 根据用户ID查询指定用户的信息
     * @param userId 目标用户的ID
     * @return ApiResponse<UserInfo> 包含过滤后用户信息的响应
     */
    @Operation(summary = "根据用户ID查询信息", description = "根据用户ID查询指定用户的信息，并根据可见性设置过滤信息。")
    @GetMapping("/{userId}")
    public ApiResponse<UserInfo> getUserInfoById(
            @Parameter(description = "目标用户的ID") @PathVariable Long userId) {
        Long myUid = UidUtils.getUid();
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        if (userInfo == null) {
            return ApiResponse.fail("用户不存在");
        }
        UserInfo filtered = filterUserInfoByVisibility(userInfo, myUid);
        return ApiResponse.success(filtered);
    }
    @Operation(summary = "根据用户ID获取头像URL", description = "根据用户ID查询指定用户的信息，并根据可见性设置过滤信息。")
    @GetMapping("/profileurl/{userId}")
    public ApiResponse<String> getUserProfilePicture(
            @Parameter(description = "目标用户的ID") @PathVariable Long userId) {
        UserInfo userInfo = userInfoService.getUserInfo(userId);
        if (userInfo == null) {
            return ApiResponse.fail("用户不存在");
        }
        return ApiResponse.success(userInfo.getAvatarUrl());
    }
    /**
     * 根据用户的可见性配置过滤用户信息
     * @param userInfo 待过滤的用户信息对象
     * @param visitorUid 当前访问者的用户ID
     * @return filtered 过滤后的用户信息对象
     */
    private UserInfo filterUserInfoByVisibility(UserInfo userInfo, Long visitorUid) {
        if (userInfo == null) return null;
//        if (userInfo.getUserId().equals(visitorUid)) return userInfo;

//        String visibilityJson = userInfo.getInfoVisibility();
//        if (visibilityJson == null || visibilityJson.isEmpty()) return userInfo;

        try {
//            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
//            java.util.Map<String, Boolean> vis = mapper.readValue(visibilityJson, java.util.Map.class);

            UserInfo filtered = new UserInfo();
            filtered.setId(userInfo.getId());
            filtered.setUserId(userInfo.getUserId());
            filtered.setAvatarUrl(userInfo.getAvatarUrl());
            if(userInfo.getInfoVisibility().equals("public")){
               filtered.setCollege(userInfo.getCollege());
               filtered.setCampus(userInfo.getCampus());
               filtered.setGender(userInfo.getGender());
               filtered.setGrade(userInfo.getGrade());
               filtered.setMajor(userInfo.getMajor());
            }
            filtered.setStudentId(userInfo.getStudentId());
            filtered.setSignature(userInfo.getSignature());
            filtered.setInterests(userInfo.getInterests());
            filtered.setSkills(userInfo.getSkills());
            if(userInfo.getContactVisibility().equals("public")){
                 filtered.setQq(userInfo.getQq());
                 filtered.setWechat(userInfo.getWechat());
                 filtered.setPhone(userInfo.getPhone());
            }
            if(userInfo.getInfoVisibility().equals("public")){

            }
//            String contactVis = userInfo.getContactVisibility();
//            if (contactVis != null) {
//                java.util.List<String> allowedContacts = java.util.Arrays.asList(contactVis.split(","));
//                if (allowedContacts.contains("qq")) filtered.setQq(userInfo.getQq());
//                if (allowedContacts.contains("wechat")) filtered.setWechat(userInfo.getWechat());
//                if (allowedContacts.contains("phone")) filtered.setPhone(userInfo.getPhone());
//            }
            return filtered;
        } catch (Exception e) {
            return userInfo;
        }
    }
}
