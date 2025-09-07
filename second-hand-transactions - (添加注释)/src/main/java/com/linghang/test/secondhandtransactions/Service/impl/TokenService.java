package com.linghang.test.secondhandtransactions.Service.impl;

import com.linghang.test.secondhandtransactions.utils.JwtUtils;
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

    public Object getUserFromToken(String token) {
        return redisTemplate.opsForValue().get("Wust_basic_token:"+token);
    }
    public String getUid(String token){
        return (String) redisTemplate.opsForValue().get("LingHangToken:"+token);
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