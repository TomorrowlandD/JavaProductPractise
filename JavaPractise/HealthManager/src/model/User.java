package model;

/**
 * 用户认证模型类
 * 用于用户登录验证和权限管理
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role; // USER, ADMIN
    private String profileName; // 关联的用户档案名称
    private boolean isActive;
    
    // 构造方法
    public User() {}
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = true;
    }
    
    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getProfileName() { return profileName; }
    public void setProfileName(String profileName) { this.profileName = profileName; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { this.isActive = active; }
    
    // 权限判断方法
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }
    
    public boolean isUser() {
        return "USER".equals(this.role);
    }
    
    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
} 