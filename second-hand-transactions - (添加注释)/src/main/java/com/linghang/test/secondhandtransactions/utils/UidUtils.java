package com.linghang.test.secondhandtransactions.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class UidUtils {
    public static String getUsernameFromSecurityContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String username) {
            return username;
        }
        return null;
    }
}
