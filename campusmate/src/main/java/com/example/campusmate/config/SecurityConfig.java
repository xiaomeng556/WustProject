package com.example.campusmate.config;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import com.example.campusmate.Utils.JwtUtils ;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
//                                "/",                    // 首页
//                                "/index.html",          // 首页文件
//                                "/static/**",           // 静态资源
//                                "/favicon.ico",
//                                "api/activities",
//                                "api/activities/**",
//                                "api/comments",
//                                "api/comments/**",
//                                "api/drafts",
//                                "api/drafts/**",
//                                "api/notifications",
//                                "api/notifications/**",
//                                "api/user",
//                                "api/user/**",
//                                "api/user/info",
//                                "api/user/info/**",
//                                "api/user/settings",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
