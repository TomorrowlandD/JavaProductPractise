package model;

import java.time.LocalDate;

/**
 * 用户档案数据模型类
 * 用于存储和管理用户的基本健康信息
 */
public class UserProfile {
    // 基本个人信息
    private String name;              // 姓名
    private int age;                  // 年龄
    private String gender;            // 性别
    private double height;            // 身高(cm)
    private double weight;            // 体重(kg)
    
    // 健康目标信息
    private double targetWeight;      // 目标体重(kg)
    private String fitnessGoal;       // 健身目标
    
    // 联系信息
    private String email;             // 邮箱
    private String phone;             // 电话
    
    // 系统信息
    private LocalDate createdDate;    // 创建日期
    private LocalDate lastUpdated;    // 最后更新日期
    
    // 默认构造方法
    public UserProfile() {
        this.createdDate = LocalDate.now();
        this.lastUpdated = LocalDate.now();
    }
    
    // 带参构造方法
    public UserProfile(String name, int age, String gender, double height, double weight) {
        this();
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
    }
    
    // 完整构造方法
    public UserProfile(String name, int age, String gender, double height, double weight,
                      double targetWeight, String fitnessGoal, String email, String phone) {
        this(name, age, gender, height, weight);
        this.targetWeight = targetWeight;
        this.fitnessGoal = fitnessGoal;
        this.email = email;
        this.phone = phone;
    }
    
    // Getter和Setter方法
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.lastUpdated = LocalDate.now();
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
        this.lastUpdated = LocalDate.now();
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
        this.lastUpdated = LocalDate.now();
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
        this.lastUpdated = LocalDate.now();
    }
    
    public double getWeight() {
        return weight;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
        this.lastUpdated = LocalDate.now();
    }
    
    public double getTargetWeight() {
        return targetWeight;
    }
    
    public void setTargetWeight(double targetWeight) {
        this.targetWeight = targetWeight;
        this.lastUpdated = LocalDate.now();
    }
    
    public String getFitnessGoal() {
        return fitnessGoal;
    }
    
    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
        this.lastUpdated = LocalDate.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.lastUpdated = LocalDate.now();
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.lastUpdated = LocalDate.now();
    }
    
    public LocalDate getCreatedDate() {
        return createdDate;
    }
    
    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
    
    // 工具方法
    /**
     * 计算BMI指数
     * @return BMI值
     */
    public double calculateBMI() {
        if (height <= 0 || weight <= 0) {
            return 0.0;
        }
        double heightInMeters = height / 100.0;
        return weight / (heightInMeters * heightInMeters);
    }
    
    /**
     * 获取BMI分类
     * @return BMI分类字符串
     */
    public String getBMICategory() {
        double bmi = calculateBMI();
        if (bmi < 18.5) {
            return "偏瘦";
        } else if (bmi < 24.0) {
            return "正常";
        } else if (bmi < 28.0) {
            return "超重";
        } else {
            return "肥胖";
        }
    }
    
    /**
     * 计算距离目标体重的差值
     * @return 需要增加或减少的体重(kg)，正值表示需要减重，负值表示需要增重
     */
    public double getWeightDifference() {
        return weight - targetWeight;
    }
    
    /**
     * 检查用户信息是否完整
     * @return 如果基本信息完整返回true，否则返回false
     */
    public boolean isProfileComplete() {
        return name != null && !name.trim().isEmpty() &&
               age > 0 &&
               gender != null && !gender.trim().isEmpty() &&
               height > 0 &&
               weight > 0;
    }
    
    @Override
    public String toString() {
        return String.format("UserProfile{姓名='%s', 年龄=%d, 性别='%s', 身高=%.1fcm, 体重=%.1fkg, BMI=%.1f(%s)}",
                name, age, gender, height, weight, calculateBMI(), getBMICategory());
    }
} 