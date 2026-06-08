package com.shanxi.shopping.client.common;

import com.shanxi.shopping.client.ui.MainFrame;

/**
 * 应用上下文（单例）— 持有全局状态：当前用户、Token、导航控制
 */
public class AppContext {

    private static final AppContext INSTANCE = new AppContext();

    private MainFrame mainFrame;
    private String token;
    private Long userId;
    private String username;
    private String role;        // "USER" 或 "SELLER"
    private String shopName;

    /** 跨页面临时传参（如 orderId、productId） */
    private final java.util.Map<String, Object> attributes = new java.util.HashMap<>();

    private AppContext() {}

    public void setAttribute(String key, Object value) { attributes.put(key, value); }
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) { return (T) attributes.remove(key); }

    public static AppContext getInstance() {
        return INSTANCE;
    }

    // ---- getter / setter ----

    public MainFrame getMainFrame() { return mainFrame; }
    public void setMainFrame(MainFrame mainFrame) { this.mainFrame = mainFrame; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public boolean isBuyer() {
        return "USER".equals(role);
    }

    public boolean isSeller() {
        return "SELLER".equals(role);
    }

    /**
     * 导航到指定页面
     */
    public void navigateTo(String pageKey) {
        if (mainFrame != null) {
            mainFrame.navigateTo(pageKey);
        }
    }

    /**
     * 登录成功后的处理
     */
    public void onLoginSuccess(String token, Long userId, String username, String role, String shopName) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.shopName = shopName;
        mainFrame.onLoginSuccess();
    }

    /**
     * 退出登录
     */
    public void logout() {
        this.token = null;
        this.userId = null;
        this.username = null;
        this.role = null;
        this.shopName = null;
        navigateTo("login");
        mainFrame.onLogout();
    }
}
