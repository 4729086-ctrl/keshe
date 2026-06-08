package com.shanxi.shopping.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类 — 获取当前登录用户信息
 */
public class SecurityUtil {

    /**
     * 获取当前登录用户信息
     */
    public static JwtUserInfo getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtUserInfo info) {
            return info;
        }
        throw new RuntimeException("未登录或 Token 无效");
    }

    /**
     * 获取当前用户 ID
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().userId();
    }

    /**
     * 获取当前用户角色
     */
    public static String getCurrentRole() {
        return getCurrentUser().role();
    }
}
