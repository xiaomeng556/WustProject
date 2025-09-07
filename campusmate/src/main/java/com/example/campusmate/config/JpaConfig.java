package com.example.campusmate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA相关配置类
 * 启用JPA审计功能（如自动填充创建/更新时间）
 */
@Configuration
@EnableJpaAuditing // 启用JPA审计
public class JpaConfig {
    // 可扩展更多JPA相关配置
}