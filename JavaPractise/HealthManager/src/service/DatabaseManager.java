package service;

import model.UserProfile;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import model.DailyRecord;

/**
 * 数据库管理类
 * 负责MySQL数据库的连接和用户档案数据的增删改查操作
 */
public class DatabaseManager {
    
    // 数据库连接配置
    private static final String DB_URL = "jdbc:mysql://localhost:3306/health_manager";
    private static final String DB_USER = "root";  // 默认用户名，可根据实际情况修改
    private static final String DB_PASSWORD = "DGH20231505";  // 默认密码为空，可根据实际情况修改
    
    // JDBC驱动类名
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    /**
     * 获取数据库连接
     * 
     * @return 数据库连接对象
     * @throws SQLException 数据库连接异常
     */
    public static Connection getConnection() throws SQLException {
        try {
            // 加载JDBC驱动
            Class.forName(JDBC_DRIVER);
            // 建立连接
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC驱动未找到: " + e.getMessage());
        }
    }
    
    /**
     * 测试数据库连接
     * 
     * @return 连接是否成功
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("数据库连接测试失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 初始化数据库表
     * 创建user_profile表（如果不存在）
     */
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS user_profile (" +
            "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
            "name VARCHAR(50) NOT NULL UNIQUE COMMENT '姓名-唯一'," +
            "age INT NOT NULL COMMENT '年龄'," +
            "gender ENUM('男', '女') NOT NULL COMMENT '性别'," +
            "height DECIMAL(5,2) NOT NULL COMMENT '身高(cm)'," +
            "weight DECIMAL(5,2) NOT NULL COMMENT '体重(kg)'," +
            "target_weight DECIMAL(5,2) COMMENT '目标体重(kg)'," +
            "fitness_goal VARCHAR(50) COMMENT '健身目标'," +
            "health_status TEXT COMMENT '健康状况'," +
            "health_notes TEXT COMMENT '健康备注'," +
            "phone VARCHAR(20) COMMENT '联系电话'," +
            "created_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "updated_date DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
            "is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活'" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户档案表'";
        
        String createDailyRecordTableSQL = "CREATE TABLE IF NOT EXISTS daily_record (" +
            "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "user_name VARCHAR(50) NOT NULL COMMENT '用户名'," +
            "date DATE NOT NULL COMMENT '日期'," +
            "weight DECIMAL(5,2) COMMENT '体重(kg)'," +
            "exercise VARCHAR(100) COMMENT '运动内容'," +
            "exercise_duration DECIMAL(4,2) COMMENT '运动时长(小时)'," +
            "sleep_duration DECIMAL(4,2) COMMENT '睡眠时长(小时)'," +
            "mood VARCHAR(20) COMMENT '心情'," +
            "note TEXT COMMENT '备注'," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
            "FOREIGN KEY (user_name) REFERENCES user_profile(name) ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日健康记录表'";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("用户档案表检查完成");
            stmt.executeUpdate(createDailyRecordTableSQL);
            System.out.println("每日记录表检查完成");
            
        } catch (SQLException e) {
            System.err.println("数据库表初始化失败: " + e.getMessage());
            // 注释掉弹窗，避免启动时的错误提示
            // JOptionPane.showMessageDialog(null, 
            //     "数据库表初始化失败:\n" + e.getMessage(), 
            //     "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 保存用户档案到数据库
     * 如果用户已存在则更新，否则插入新记录
     * 
     * @param profile 用户档案对象
     * @return 保存是否成功
     */
    public static boolean saveUserProfile(UserProfile profile) {
        if (profile == null) {
            return false;
        }
        
        // 根据用户名检查用户是否已存在
        UserProfile existingProfile = getUserProfileByName(profile.getName());
        
        if (existingProfile != null) {
            // 如果存在，设置ID并更新
            profile.setId(existingProfile.getId());
            return updateUserProfile(profile);
        } else {
            // 如果不存在，插入新记录
            return insertUserProfile(profile);
        }
    }
    
    /**
     * 插入新的用户档案
     */
    public static boolean insertUserProfile(UserProfile profile) {
        String insertSQL = "INSERT INTO user_profile (name, age, gender, height, weight, target_weight, " +
                          "fitness_goal, health_status, health_notes, phone) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            setProfileParameters(pstmt, profile);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                // 获取生成的ID并设置到profile对象中
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        profile.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("用户档案插入成功，ID: " + profile.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("用户档案插入失败: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(null, 
                    "用户名已存在，请使用不同的用户名！", 
                    "用户名重复", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "保存用户档案失败:\n" + e.getMessage(), 
                    "数据库错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
    
    /**
     * 更新现有用户档案
     */
    public static boolean updateUserProfile(UserProfile profile) {
        String updateSQL = "UPDATE user_profile " +
                          "SET name=?, age=?, gender=?, height=?, weight=?, target_weight=?, " +
                          "fitness_goal=?, health_status=?, health_notes=?, phone=? " +
                          "WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            setProfileParameters(pstmt, profile);
            pstmt.setInt(11, profile.getId());
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("用户档案更新成功");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("用户档案更新失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "更新用户档案失败:\n" + e.getMessage(), 
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * 设置PreparedStatement的参数
     */
    private static void setProfileParameters(PreparedStatement pstmt, UserProfile profile) throws SQLException {
        pstmt.setString(1, profile.getName());
        pstmt.setInt(2, profile.getAge());
        pstmt.setString(3, profile.getGender());
        pstmt.setDouble(4, profile.getHeight());
        pstmt.setDouble(5, profile.getWeight());
        
        if (profile.getTargetWeight() > 0) {
            pstmt.setDouble(6, profile.getTargetWeight());
        } else {
            pstmt.setNull(6, Types.DECIMAL);
        }
        
        pstmt.setString(7, profile.getFitnessGoal());
        pstmt.setString(8, profile.getHealthStatus());
        pstmt.setString(9, profile.getHealthNotes());
        pstmt.setString(10, profile.getPhone());
    }
    
    /**
     * 从数据库加载用户档案
     * 目前假设只有一个用户，加载最新的激活用户档案
     * 
     * @return 用户档案对象，如果不存在则返回null
     */
    public static UserProfile loadUserProfile() {
        String selectSQL = "SELECT * FROM user_profile WHERE is_active = TRUE ORDER BY updated_date DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                UserProfile profile = new UserProfile();
                
                profile.setId(rs.getInt("id"));
                profile.setName(rs.getString("name"));
                profile.setAge(rs.getInt("age"));
                profile.setGender(rs.getString("gender"));
                profile.setHeight(rs.getDouble("height"));
                profile.setWeight(rs.getDouble("weight"));
                
                double targetWeight = rs.getDouble("target_weight");
                if (!rs.wasNull()) {
                    profile.setTargetWeight(targetWeight);
                }
                
                profile.setFitnessGoal(rs.getString("fitness_goal"));
                profile.setHealthStatus(rs.getString("health_status"));
                profile.setHealthNotes(rs.getString("health_notes"));
                profile.setPhone(rs.getString("phone"));
                
                // 设置创建和更新时间
                Timestamp createdTimestamp = rs.getTimestamp("created_date");
                if (createdTimestamp != null) {
                    profile.setCreatedDate(createdTimestamp.toLocalDateTime().toLocalDate());
                }
                
                Timestamp updatedTimestamp = rs.getTimestamp("updated_date");
                if (updatedTimestamp != null) {
                    profile.setUpdatedDate(updatedTimestamp.toLocalDateTime().toLocalDate());
                }
                
                System.out.println("用户档案加载成功");
                return profile;
            }
            
        } catch (SQLException e) {
            System.err.println("用户档案加载失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "加载用户档案失败:\n" + e.getMessage(), 
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    /**
     * 检查数据库中是否存在用户档案数据
     * 
     * @return 是否存在数据
     */
    public static boolean hasUserProfileData() {
        String countSQL = "SELECT COUNT(*) FROM user_profile WHERE is_active = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(countSQL);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("检查用户档案数据失败: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * 删除用户档案（软删除，设置is_active为false）
     * 
     * @return 删除是否成功
     */
    public static boolean deleteUserProfile() {
        String deleteSQL = "UPDATE user_profile SET is_active = FALSE WHERE is_active = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("用户档案删除成功");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("用户档案删除失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "删除用户档案失败:\n" + e.getMessage(), 
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * 获取所有用户档案
     */
    public static List<UserProfile> getAllUserProfiles() {
        List<UserProfile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM user_profile ORDER BY id ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UserProfile profile = new UserProfile();
                profile.setId(rs.getInt("id"));
                profile.setName(rs.getString("name"));
                profile.setAge(rs.getInt("age"));
                profile.setGender(rs.getString("gender"));
                profile.setHeight(rs.getDouble("height"));
                profile.setWeight(rs.getDouble("weight"));
                profile.setTargetWeight(rs.getDouble("target_weight"));
                profile.setFitnessGoal(rs.getString("fitness_goal"));
                profile.setHealthStatus(rs.getString("health_status"));
                profile.setHealthNotes(rs.getString("health_notes"));
                profile.setPhone(rs.getString("phone"));
                profiles.add(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profiles;
    }
    
    /**
     * 根据ID获取用户档案
     */
    public static UserProfile getUserProfileById(int id) {
        String sql = "SELECT * FROM user_profile WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();
                    profile.setId(rs.getInt("id"));
                    profile.setName(rs.getString("name"));
                    profile.setAge(rs.getInt("age"));
                    profile.setGender(rs.getString("gender"));
                    profile.setHeight(rs.getDouble("height"));
                    profile.setWeight(rs.getDouble("weight"));
                    profile.setTargetWeight(rs.getDouble("target_weight"));
                    profile.setFitnessGoal(rs.getString("fitness_goal"));
                    profile.setHealthStatus(rs.getString("health_status"));
                    profile.setHealthNotes(rs.getString("health_notes"));
                    profile.setPhone(rs.getString("phone"));
                    return profile;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 根据ID删除用户档案
     */
    public static boolean deleteUserProfileById(int id) {
        String sql = "DELETE FROM user_profile WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 根据用户名获取用户档案
     */
    public static UserProfile getUserProfileByName(String name) {
        String sql = "SELECT * FROM user_profile WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UserProfile profile = new UserProfile();
                    profile.setId(rs.getInt("id"));
                    profile.setName(rs.getString("name"));
                    profile.setAge(rs.getInt("age"));
                    profile.setGender(rs.getString("gender"));
                    profile.setHeight(rs.getDouble("height"));
                    profile.setWeight(rs.getDouble("weight"));
                    profile.setTargetWeight(rs.getDouble("target_weight"));
                    profile.setFitnessGoal(rs.getString("fitness_goal"));
                    profile.setHealthStatus(rs.getString("health_status"));
                    profile.setHealthNotes(rs.getString("health_notes"));
                    profile.setPhone(rs.getString("phone"));
                    return profile;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 保存每日记录到数据库
     * @param record DailyRecord对象
     * @return 保存是否成功
     */
    public static boolean saveDailyRecord(model.DailyRecord record) {
        if (record == null) return false;
        String insertSQL = "INSERT INTO daily_record (user_name, date, weight, exercise, exercise_duration, sleep_duration, mood, note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, record.getUserName());
            pstmt.setDate(2, java.sql.Date.valueOf(record.getDate()));
            pstmt.setDouble(3, record.getWeight());
            pstmt.setString(4, record.getExercise());
            pstmt.setDouble(5, record.getExerciseDuration());
            pstmt.setDouble(6, record.getSleepDuration());
            pstmt.setString(7, record.getMood());
            pstmt.setString(8, record.getNote());
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("每日记录保存失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "保存每日记录失败:\n" + e.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * 获取所有每日记录
     */
    public static List<DailyRecord> getAllDailyRecords() {
        List<DailyRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM daily_record ORDER BY date DESC, id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                DailyRecord record = new DailyRecord();
                record.setId(rs.getInt("id"));
                record.setUserName(rs.getString("user_name"));
                record.setDate(rs.getDate("date").toLocalDate());
                record.setWeight(rs.getDouble("weight"));
                record.setExercise(rs.getString("exercise"));
                record.setExerciseDuration(rs.getDouble("exercise_duration"));
                record.setSleepDuration(rs.getDouble("sleep_duration"));
                record.setMood(rs.getString("mood"));
                record.setNote(rs.getString("note"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 更新每日记录
     */
    public static boolean updateDailyRecord(model.DailyRecord record) {
        if (record == null || record.getId() == 0) return false;
        String sql = "UPDATE daily_record SET user_name=?, date=?, weight=?, exercise=?, exercise_duration=?, sleep_duration=?, mood=?, note=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, record.getUserName());
            pstmt.setDate(2, java.sql.Date.valueOf(record.getDate()));
            pstmt.setDouble(3, record.getWeight());
            pstmt.setString(4, record.getExercise());
            pstmt.setDouble(5, record.getExerciseDuration());
            pstmt.setDouble(6, record.getSleepDuration());
            pstmt.setString(7, record.getMood());
            pstmt.setString(8, record.getNote());
            pstmt.setInt(9, record.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据ID删除每日记录
     */
    public static boolean deleteDailyRecordById(int id) {
        String sql = "DELETE FROM daily_record WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 