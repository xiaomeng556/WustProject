package com.example.campusmate.Utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UidUtils {
    public static Long getUid() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String uidStr) {
            try {
                return Long.parseLong(uidStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public static String getStudentId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // 假设principal为json字符串或自定义对象时可扩展
        // 这里只做简单示例：如果principal是"userId:studentId"格式
        if (principal instanceof String str) {
            // 例："123456:20240001"
            String[] parts = str.split(":");
            if (parts.length == 2) {
                return parts[1];
            }
        }
        return null;
    }
}
