package com.example.campusmate.service;

import com.example.campusmate.Utils.JwtUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class TokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtUtils jwtUtils;
    public TokenService(RedisTemplate<String, Object> redisTemplate, JwtUtils jwtUtils) {
        this.redisTemplate = redisTemplate;
        this.jwtUtils = jwtUtils;
    }
    // 生成Token并缓存用户信息，缓存过期时间1天
    public String createToken(String userId, Object userInfo) {
        String token = jwtUtils.generateToken(userId);
        redisTemplate.opsForValue().set("Wust_basic_token:" + token, userInfo, 1, TimeUnit.DAYS);
        createUidToken(token,userId);
        return token;
    }
    public Object getUserFromToken(String token) {
        return redisTemplate.opsForValue().get("Wust_basic_token:"+token);
    }
    //学号缓存
    public boolean createUidToken(String token,String username){
        redisTemplate.opsForValue().set("LingHangToken:"+token,username,1, TimeUnit.DAYS);
        return true;
    }

    public String getUid(String token){
        return (String) redisTemplate.opsForValue().get("LingHangToken:"+token);
    }
    //姓名缓存
    public boolean createName(String token,String username){
        redisTemplate.opsForValue().set("Student_name:"+token,username,1, TimeUnit.DAYS);
        return true;
    }
    public String getName(String token) {
        return (String) redisTemplate.opsForValue().get("Student_name:"+token);
    }
    // 根据token从Redis获取用户信息
    // 删除Redis中保存的token，实现注销
    public void deleteToken(String token) {
        redisTemplate.delete("Wust_basic_token:" + token);
        deleteUid(token);
        deleteName(token);
    }
    public void deleteUid(String token) {
        redisTemplate.delete("LingHangToken:" + token);
    }
    public void deleteName(String token) {
        redisTemplate.delete("Student_name:" + token);
    }

}
