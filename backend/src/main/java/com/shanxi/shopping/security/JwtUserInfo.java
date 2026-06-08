package com.shanxi.shopping.security;

/**
 * JWT 中携带的用户信息，存储在 SecurityContext.details 中
 */
public record JwtUserInfo(Long userId, String username, String role) {
}
