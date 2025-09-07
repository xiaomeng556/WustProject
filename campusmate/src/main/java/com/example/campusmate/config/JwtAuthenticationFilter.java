package com.example.campusmate.config;
import com.example.campusmate.service.TokenService ;
import com.example.campusmate.Utils.JwtUtils;
import com.example.campusmate.entity.UserInfo;
import com.example.campusmate.repository.UserInfoRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    @Autowired
    TokenService tokenService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Wuster ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                String userIdStr = jwtUtils.getUserIdFromToken(token);
                // 自动插入UserInfo
                try {
                    Long uid = Long.parseLong(userIdStr);
                    if (userInfoRepository.findByUserId(uid) == null) {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setUserId(uid);
                        userInfoRepository.save(userInfo);
                    }
                } catch (Exception ignored) {}
                // 只放userId字符串到principal，不查数据库
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userIdStr, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
