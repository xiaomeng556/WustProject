package com.example.campusmate.repository;

import com.example.campusmate.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户信息表数据库操作接口
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    /**
     * 根据App用户ID查询用户信息
     * @param userId App用户ID
     * @return 用户信息
     */
    UserInfo findByUserId(Long userId);
}