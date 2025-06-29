package service;

import model.User;
import model.UserProfile;

/**
 * 会话管理器
 * 管理当前登录用户的会话状态
 */
public class SessionManager {
    private static User currentUser;
    private static UserProfile currentProfile;
    
    /**
     * 用户登录
     */
    public static boolean login(String username, String password) {
        User user = DatabaseManager.authenticateUser(username, password);
        if (user != null && user.isActive()) {
            currentUser = user;
            // 无论profileName是否为null，都用用户名查档案
            currentProfile = DatabaseManager.getUserProfileByName(user.getUsername());
            System.out.println("用户登录成功: " + username);
            return true;
        }
        System.out.println("登录失败: 用户名或密码错误");
        return false;
    }
    
    /**
     * 用户登出
     */
    public static void logout() {
        System.out.println("用户登出: " + (currentUser != null ? currentUser.getUsername() : "未知"));
        currentUser = null;
        currentProfile = null;
    }
    
    /**
     * 检查是否已登录
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * 检查当前用户是否为管理员
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * 检查当前用户是否为普通用户
     */
    public static boolean isUser() {
        return currentUser != null && currentUser.isUser();
    }
    
    /**
     * 获取当前登录用户
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * 获取当前用户的档案
     */
    public static UserProfile getCurrentProfile() {
        return currentProfile;
    }
    
    /**
     * 设置当前用户档案
     */
    public static void setCurrentProfile(UserProfile profile) {
        currentProfile = profile;
        // 同时更新用户的档案关联
        if (currentUser != null && profile != null) {
            currentUser.setProfileName(profile.getName());
        }
    }
    
    /**
     * 获取当前用户显示名称
     */
    public static String getCurrentUserDisplayName() {
        if (currentUser == null) return "未登录";
        if (currentProfile != null) {
            return currentProfile.getName() + " (" + currentUser.getRole() + ")";
        }
        return currentUser.getUsername() + " (" + currentUser.getRole() + ")";
    }
} 