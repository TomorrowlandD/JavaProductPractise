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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/health_manager";  //数据库连接的URL
    private static final String DB_USER = "root";   //数据库连接的主机
    private static final String DB_PASSWORD = "DGH20231505";    //数据库连接的密码
    
    // JDBC驱动类名
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  //驱动类的名称
    
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
     * 创建各种表(这样在别的电脑上也能运行)
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
            "UNIQUE KEY uniq_user_date (user_name, date)," +
            "FOREIGN KEY (user_name) REFERENCES user_profile(name) ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日健康记录表'";
        
        String createExercisePlanTableSQL = "CREATE TABLE IF NOT EXISTS exercise_plan (" +
            "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID'," +
            "user_name VARCHAR(50) NOT NULL COMMENT '用户名'," +
            "exercise_type VARCHAR(50) NOT NULL COMMENT '运动类型'," +
            "plan_date DATE NOT NULL COMMENT '计划日期'," +
            "duration DECIMAL(4,2) COMMENT '计划时长(小时)'," +
            "intensity VARCHAR(20) COMMENT '运动强度(低/中/高)'," +
            "is_completed BOOLEAN DEFAULT FALSE COMMENT '是否完成'," +
            "actual_duration DECIMAL(4,2) COMMENT '实际完成时长(小时)'," +
            "notes TEXT COMMENT '备注'," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
            "UNIQUE KEY uniq_user_plan_date (user_name, plan_date)," +
            "FOREIGN KEY (user_name) REFERENCES user_profile(name) ON DELETE CASCADE ON UPDATE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动计划表'";
        
        String createDietRecordTableSQL = "CREATE TABLE IF NOT EXISTS diet_record (" +
            "id INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，自增'," +
            "user_name VARCHAR(50) NOT NULL COMMENT '用户名，关联user_profile表'," +
            "record_date DATE NOT NULL COMMENT '饮食记录日期'," +
            "breakfast TEXT COMMENT '早餐内容（多选食物、其它、无安排）'," +
            "lunch TEXT COMMENT '午餐内容（多选食物、其它、无安排）'," +
            "dinner TEXT COMMENT '晚餐内容（多选食物、其它、无安排）'," +
            "notes TEXT COMMENT '备注'," +
            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
            "INDEX idx_user_date (user_name, record_date)," +
            "CONSTRAINT fk_diet_user FOREIGN KEY (user_name) REFERENCES user_profile(name) ON DELETE CASCADE" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录表';";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(createTableSQL);
            System.out.println("用户档案表检查完成");
            stmt.executeUpdate(createDailyRecordTableSQL);
            System.out.println("每日记录表检查完成");
            stmt.executeUpdate(createExercisePlanTableSQL);
            System.out.println("运动计划表检查完成");
            stmt.executeUpdate(createDietRecordTableSQL);
            System.out.println("饮食记录表检查完成");
            
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
    
    // ==================== 运动计划相关操作 ====================
    
    /**
     * 插入新的运动计划
     */
    public static boolean insertExercisePlan(model.ExercisePlan plan) {
        String insertSQL = "INSERT INTO exercise_plan (user_name, exercise_type, plan_date, duration, intensity, is_completed, actual_duration, notes) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            
            setExercisePlanParameters(pstmt, plan);
            
            int result = pstmt.executeUpdate();
            if (result > 0) {
                // 获取生成的ID并设置到plan对象中
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        plan.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("运动计划插入成功，ID: " + plan.getId());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("运动计划插入失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "保存运动计划失败:\n" + e.getMessage(), 
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
        
        return false;
    }
    
    /**
     * 更新运动计划
     */
    public static boolean updateExercisePlan(model.ExercisePlan plan) {
        String updateSQL = "UPDATE exercise_plan " +
                          "SET user_name=?, exercise_type=?, plan_date=?, duration=?, intensity=?, " +
                          "is_completed=?, actual_duration=?, notes=? " +
                          "WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            setExercisePlanParameters(pstmt, plan);
            pstmt.setInt(9, plan.getId());
            int result = pstmt.executeUpdate();
            if (result > 0) {
                System.out.println("运动计划更新成功");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("运动计划更新失败: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "更新运动计划失败:\n" + e.getMessage(), 
                "数据库错误", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**
     * 设置运动计划PreparedStatement的参数
     */
    private static void setExercisePlanParameters(PreparedStatement pstmt, model.ExercisePlan plan) throws SQLException {
        pstmt.setString(1, plan.getUserName());
        pstmt.setString(2, plan.getExerciseType());
        pstmt.setDate(3, java.sql.Date.valueOf(plan.getPlanDate()));
        
        if (plan.getDuration() != null) {
            pstmt.setDouble(4, plan.getDuration());
        } else {
            pstmt.setNull(4, Types.DECIMAL);
        }
        
        pstmt.setString(5, plan.getIntensity());
        pstmt.setBoolean(6, plan.isCompleted());
        
        if (plan.getActualDuration() != null) {
            pstmt.setDouble(7, plan.getActualDuration());
        } else {
            pstmt.setNull(7, Types.DECIMAL);
        }
        
        pstmt.setString(8, plan.getNotes());
    }
    
    /**
     * 获取所有运动计划
     */
    public static List<model.ExercisePlan> getAllExercisePlans() {
        List<model.ExercisePlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM exercise_plan ORDER BY plan_date DESC, id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                model.ExercisePlan plan = new model.ExercisePlan();
                plan.setId(rs.getInt("id"));
                plan.setUserName(rs.getString("user_name"));
                plan.setExerciseType(rs.getString("exercise_type"));
                plan.setPlanDate(rs.getDate("plan_date").toLocalDate());
                
                double duration = rs.getDouble("duration");
                if (!rs.wasNull()) {
                    plan.setDuration(duration);
                }
                
                plan.setIntensity(rs.getString("intensity"));
                plan.setCompleted(rs.getBoolean("is_completed"));
                
                double actualDuration = rs.getDouble("actual_duration");
                if (!rs.wasNull()) {
                    plan.setActualDuration(actualDuration);
                }
                
                plan.setNotes(rs.getString("notes"));
                plan.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                plan.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                
                plans.add(plan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }
    
    /**
     * 根据用户名获取运动计划
     */
    public static List<model.ExercisePlan> getExercisePlansByUser(String userName) {
        List<model.ExercisePlan> plans = new ArrayList<>();
        String sql = "SELECT * FROM exercise_plan WHERE user_name = ? ORDER BY plan_date DESC, id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    model.ExercisePlan plan = new model.ExercisePlan();
                    plan.setId(rs.getInt("id"));
                    plan.setUserName(rs.getString("user_name"));
                    plan.setExerciseType(rs.getString("exercise_type"));
                    plan.setPlanDate(rs.getDate("plan_date").toLocalDate());
                    
                    double duration = rs.getDouble("duration");
                    if (!rs.wasNull()) {
                        plan.setDuration(duration);
                    }
                    
                    plan.setIntensity(rs.getString("intensity"));
                    plan.setCompleted(rs.getBoolean("is_completed"));
                    
                    double actualDuration = rs.getDouble("actual_duration");
                    if (!rs.wasNull()) {
                        plan.setActualDuration(actualDuration);
                    }
                    
                    plan.setNotes(rs.getString("notes"));
                    plan.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    plan.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    
                    plans.add(plan);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plans;
    }
    
    /**
     * 根据ID删除运动计划
     */
    public static boolean deleteExercisePlanById(int id) {
        String sql = "DELETE FROM exercise_plan WHERE id = ?";
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
     * 更新运动计划完成状态
     */
    public static boolean updatePlanCompletionStatus(int id, boolean isCompleted) {
        String sql = "UPDATE exercise_plan SET is_completed = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isCompleted);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 更新运动计划完成状态和实际时长
     */
    public static boolean updatePlanCompletionStatus(int id, boolean isCompleted, Double actualDuration) {
        String sql = "UPDATE exercise_plan SET is_completed = ?, actual_duration = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, isCompleted);
            if (actualDuration != null) {
                pstmt.setDouble(2, actualDuration);
            } else {
                pstmt.setNull(2, Types.DECIMAL);
            }
            pstmt.setInt(3, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== 饮食记录相关操作 ====================
    /**
     * 插入新的饮食记录
     */
    public static boolean insertDietRecord(model.DietRecord record) {
        if (record == null) return false;
        model.DietRecord.ValidationResult result = record.validateRecord();
        if (!result.isValid()) {
            System.err.println("数据验证失败: " + result.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "数据验证失败: " + result.getMessage(), "数据错误", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String insertSQL = "INSERT INTO diet_record (user_name, record_date, breakfast, lunch, dinner, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            setDietRecordParameters(pstmt, record);
            int resultNum = pstmt.executeUpdate();
            if (resultNum > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("饮食记录插入成功，ID: " + record.getId());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("饮食记录插入失败: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "饮食记录插入失败:\n" + e.getMessage(), "数据库错误", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * 更新饮食记录
     */
    public static boolean updateDietRecord(model.DietRecord record) {
        if (record == null || record.getId() == 0) return false;
        model.DietRecord.ValidationResult result = record.validateRecord();
        if (!result.isValid()) {
            System.err.println("数据验证失败: " + result.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "数据验证失败: " + result.getMessage(), "数据错误", javax.swing.JOptionPane.WARNING_MESSAGE);
            return false;
        }
        String updateSQL = "UPDATE diet_record SET user_name=?, record_date=?, breakfast=?, lunch=?, dinner=?, notes=? WHERE id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            setDietRecordParameters(pstmt, record);
            pstmt.setInt(7, record.getId());
            int resultNum = pstmt.executeUpdate();
            if (resultNum > 0) {
                System.out.println("饮食记录更新成功");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("饮食记录更新失败: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "饮食记录更新失败:\n" + e.getMessage(), "数据库错误", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * 删除饮食记录
     */
    public static boolean deleteDietRecordById(int id) {
        if (id <= 0) return false;
        String sql = "DELETE FROM diet_record WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "删除饮食记录失败:\n" + e.getMessage(), "数据库错误", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * 查询某用户所有饮食记录
     */
    public static java.util.List<model.DietRecord> getDietRecordsByUser(String userName) {
        if (userName == null || userName.trim().isEmpty()) return new java.util.ArrayList<>();
        java.util.List<model.DietRecord> records = new java.util.ArrayList<>();
        String sql = "SELECT * FROM diet_record WHERE user_name = ? ORDER BY record_date DESC, id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    records.add(parseDietRecordFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "查询饮食记录失败:\n" + e.getMessage(), "数据库错误", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return records;
    }

    /**
     * 查询单条饮食记录
     */
    public static model.DietRecord getDietRecordById(int id) {
        if (id <= 0) return null;
        String sql = "SELECT * FROM diet_record WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return parseDietRecordFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "查询饮食记录失败:\n" + e.getMessage(), "数据库错误", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * 设置饮食记录PreparedStatement参数
     */
    private static void setDietRecordParameters(PreparedStatement pstmt, model.DietRecord record) throws SQLException {
        pstmt.setString(1, record.getUserName());
        pstmt.setDate(2, java.sql.Date.valueOf(record.getRecordDate()));
        pstmt.setString(3, record.getBreakfast());
        pstmt.setString(4, record.getLunch());
        pstmt.setString(5, record.getDinner());
        pstmt.setString(6, record.getNotes());
    }

    /**
     * 从ResultSet解析DietRecord对象
     */
    private static model.DietRecord parseDietRecordFromResultSet(ResultSet rs) throws SQLException {
        model.DietRecord record = new model.DietRecord();
        record.setId(rs.getInt("id"));
        record.setUserName(rs.getString("user_name"));
        record.setRecordDate(rs.getDate("record_date").toLocalDate());
        record.setBreakfast(rs.getString("breakfast"));
        record.setLunch(rs.getString("lunch"));
        record.setDinner(rs.getString("dinner"));
        record.setNotes(rs.getString("notes"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) record.setCreatedAt(created.toLocalDateTime());
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) record.setUpdatedAt(updated.toLocalDateTime());
        return record;
    }

    // ==================== 数据统计相关操作 ====================
    
    // 注意：以下统计相关方法已被删除，因为项目中没有实际使用：
    // - calculateHealthStats() - 复杂的健康统计计算
    // - calculateHealthScore() - 健康评分计算  
    // - generateHealthRecommendations() - 健康建议生成
    // - calculateExerciseStats() - 复杂的运动统计计算
    // - calculateDietStats() - 复杂的饮食统计计算
    // 当前统计功能直接在DataAnalysisPanel中通过DailyRecord/ExercisePlan/DietRecord列表实现

    /**
     * 根据用户名获取每日记录
     */
    public static List<DailyRecord> getDailyRecordsByUser(String userName) {
        List<DailyRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM daily_record WHERE user_name = ? ORDER BY date DESC, id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userName);
            try (ResultSet rs = pstmt.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
} 