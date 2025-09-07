package com.example.campusmate.service.impl;

import com.example.campusmate.entity.UserInfo;
import com.example.campusmate.repository.UserInfoRepository;
import com.example.campusmate.service.UserInfoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = userInfoRepository.findByUserId(userId);
        if (userInfo == null) {
            // 新建用户信息
            userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setStudentId(userId + "");
            // 设置默认空值而不是默认文本，允许字段为空
            userInfo.setCampus(null);
            userInfo.setCollege(null);
            userInfo.setMajor(null);
            userInfo.setGrade(null);
            userInfo.setGender(null);
            // 设置默认的联系方式
            userInfo.setQq("");
            userInfo.setWechat("");
            userInfo.setPhone("");

            // 设置默认的可见性设置
            userInfo.setInfoVisibility("{}");
            userInfo.setContactVisibility("{}");

            // 设置默认头像
            userInfo.setAvatarUrl("https://cdn.jsdelivr.net/npm/element-plus@2.4.4/dist/images/avatar.jpg");

            userInfoRepository.save(userInfo);
        }
        return userInfo;
    }

    @Override
    @Transactional
    public boolean updateUserInfo(Long userId, UserInfo userInfo) {
        logger.debug("Updating user info for userId: {}", userId);
        logger.debug("Incoming userInfo infoVisibility: {}", userInfo.getInfoVisibility());
        logger.debug("Incoming userInfo contactVisibility: {}", userInfo.getContactVisibility());
        UserInfo old = userInfoRepository.findByUserId(userId);
        //传入数据预处理
        //校验联系方式至少有一个
        String infoVisibility = userInfo.getInfoVisibility();
        String contactVisibility = userInfo.getContactVisibility();
        String qq = userInfo.getQq();
        String wechat = userInfo.getWechat();
        String phone = userInfo.getPhone();
        if ((qq == null || qq.isEmpty()) && (wechat == null || wechat.isEmpty()) && (phone == null || phone.isEmpty())) {
            return false;
        }
        String avatarUrl = userInfo.getAvatarUrl();
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            avatarUrl = "https://cdn.jsdelivr.net/npm/element-plus@2.4.4/dist/images/avatar.jpg";
        }
        // 处理联系方式
        if (qq != null && qq.isEmpty()) qq = null;
        if (wechat != null && wechat.isEmpty()) wechat = null;
        if (phone != null && phone.isEmpty()) phone = null;
        //处理可见性
        if (infoVisibility != null && infoVisibility.isEmpty()) infoVisibility = "private";
        if (contactVisibility != null && contactVisibility.isEmpty()) contactVisibility = "private";
        if(old == null){
            // 新增
            userInfo.setUserId(userId); // 用token解析出来的id
            // 处理头像URL
            userInfo.setAvatarUrl(avatarUrl);
            userInfo.setQq(qq);
            userInfo.setWechat(wechat);
            userInfo.setPhone(phone);
            userInfo.setContactVisibility(contactVisibility);
            userInfo.setInfoVisibility(infoVisibility);
            userInfoRepository.save(userInfo);
            return true;
        }else{
            // 更新所有字段
            old.setCollege(userInfo.getCollege());
            old.setCampus(userInfo.getCampus());
            old.setGender(userInfo.getGender());
            old.setGrade(userInfo.getGrade());
            old.setMajor(userInfo.getMajor());
            // 处理头像URL
            old.setAvatarUrl(avatarUrl);
            old.setSignature(userInfo.getSignature());
            old.setInterests(userInfo.getInterests());
            old.setSkills(userInfo.getSkills());
            // 处理联系方式
            old.setQq(qq);
            old.setWechat(wechat);
            old.setPhone(phone);
            old.setContactVisibility(contactVisibility);
            old.setInfoVisibility(infoVisibility);
            userInfoRepository.save(old);
            return true;
        }
    }

    @Override
    public boolean updateVisibility(Long userId, String visibility) {
        UserInfo old = userInfoRepository.findByUserId(userId);
        if (old == null) return false;

        // 确保 visibility 是有效的 JSON
        String validVisibility = validateAndFormatJson(visibility, "{}");
        old.setInfoVisibility(validVisibility);
        userInfoRepository.save(old);
        return true;
    }

    @Override
    public boolean updateContactInfo(Long userId, String qq, String wechat, String phone, String contactVisibility) {
        UserInfo old = userInfoRepository.findByUserId(userId);
        if (old == null) return false;

        old.setQq(qq);
        old.setWechat(wechat);
        old.setPhone(phone);

        // 确保 contactVisibility 是有效的 JSON
        String validContactVisibility = validateAndFormatJson(contactVisibility, "{}");
        old.setContactVisibility(validContactVisibility);

        userInfoRepository.save(old);
        return true;
    }

    /**
     * 验证并格式化 JSON 字符串
     * @param value 待验证的值
     * @param defaultValue 默认值
     * @return 有效的 JSON 字符串
     */
    private String validateAndFormatJson(String value, String defaultValue) {
        // 处理 null 或空值
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }

        // 特殊处理 "Invalid value." 这种明显错误的值
        if ("Invalid value.".equals(value)) {
            logger.warn("Received 'Invalid value.' for JSON field, using default: {}", defaultValue);
            return defaultValue;
        }

        try {
            // 验证是否已经是有效的 JSON
            objectMapper.readTree(value);
            return value;
        } catch (JsonProcessingException e) {
            logger.warn("Invalid JSON value: {}, using default: {}", value, defaultValue);
            // 如果不是有效的 JSON，尝试将其转换为 JSON 字符串
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException jsonException) {
                logger.error("Failed to convert value to JSON string, using default: {}", defaultValue);
                return defaultValue;
            }
        }
    }
}
