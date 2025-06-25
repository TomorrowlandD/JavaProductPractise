package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 用户档案数据模型类
 * 用于存储和管理用户的基本健康信息
 */
public class UserProfile implements Serializable {
    private static final long serialVersionUID = 1L;
    // 数据验证常量
    public static final int MIN_AGE = 1;
    public static final int MAX_AGE = 120;
    public static final double MIN_HEIGHT = 50.0;  // cm
    public static final double MAX_HEIGHT = 250.0; // cm
    public static final double MIN_WEIGHT = 20.0;  // kg
    public static final double MAX_WEIGHT = 300.0; // kg
    
    // 基本个人信息
    private String name;              // 姓名
    private int age;                  // 年龄
    private String gender;            // 性别
    private double height;            // 身高(cm)
    private double weight;            // 体重(kg)
    
    // 健康目标信息
    private double targetWeight;      // 目标体重(kg)
    private String fitnessGoal;       // 健身目标
    
    // 联系和备注信息
    private String phone;             // 电话（可选）
    private String healthNotes;       // 健康备注（可选）
    private String healthStatus;      // 健康状况（复选框选择结果）
    private LocalDate targetDate;     // 目标达成日期
    
    // 系统信息
    private LocalDate createdDate;    // 创建日期
    private LocalDate lastUpdated;    // 最后更新日期
    
    private int id;
    
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
                      double targetWeight, String fitnessGoal, String phone, String healthNotes) {
        this(name, age, gender, height, weight);
        this.targetWeight = targetWeight;
        this.fitnessGoal = fitnessGoal;
        this.phone = phone;
        this.healthNotes = healthNotes;
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
    
    public String getHealthNotes() {
        return healthNotes;
    }
    
    public void setHealthNotes(String healthNotes) {
        this.healthNotes = healthNotes;
        this.lastUpdated = LocalDate.now();
    }
    
    public String getHealthStatus() {
        return healthStatus;
    }
    
    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
        this.lastUpdated = LocalDate.now();
    }
    
    public LocalDate getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
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
    
    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDate getLastUpdated() {
        return lastUpdated;
    }
    
    public void setUpdatedDate(LocalDate updatedDate) {
        this.lastUpdated = updatedDate;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    /**
     * 验证年龄是否在合理范围内
     * @param age 年龄值
     * @return 验证结果
     */
    public static ValidationResult validateAge(int age) {
        if (age < MIN_AGE) {
            return new ValidationResult(false, String.format("年龄不能小于%d岁", MIN_AGE));
        }
        if (age > MAX_AGE) {
            return new ValidationResult(false, String.format("年龄不能大于%d岁", MAX_AGE));
        }
        return new ValidationResult(true, "年龄有效");
    }
    
    /**
     * 验证身高是否在合理范围内
     * @param height 身高值（cm）
     * @return 验证结果
     */
    public static ValidationResult validateHeight(double height) {
        if (height < MIN_HEIGHT) {
            return new ValidationResult(false, String.format("身高不能小于%.0fcm", MIN_HEIGHT));
        }
        if (height > MAX_HEIGHT) {
            return new ValidationResult(false, String.format("身高不能大于%.0fcm", MAX_HEIGHT));
        }
        return new ValidationResult(true, "身高有效");
    }
    
    /**
     * 验证体重是否在合理范围内
     * @param weight 体重值（kg）
     * @return 验证结果
     */
    public static ValidationResult validateWeight(double weight) {
        if (weight < MIN_WEIGHT) {
            return new ValidationResult(false, String.format("体重不能小于%.0fkg", MIN_WEIGHT));
        }
        if (weight > MAX_WEIGHT) {
            return new ValidationResult(false, String.format("体重不能大于%.0fkg", MAX_WEIGHT));
        }
        return new ValidationResult(true, "体重有效");
    }
    
    /**
     * 验证目标体重的合理性
     * @param currentWeight 当前体重
     * @param targetWeight 目标体重
     * @return 验证结果
     */
    public static ValidationResult validateTargetWeight(double currentWeight, double targetWeight) {
        // 首先检查目标体重本身是否在合理范围内
        ValidationResult weightValidation = validateWeight(targetWeight);
        if (!weightValidation.isValid()) {
            return new ValidationResult(false, "目标" + weightValidation.getMessage().toLowerCase());
        }
        
        // 检查目标体重与当前体重的差值是否合理
        double difference = Math.abs(currentWeight - targetWeight);
        if (difference > 50) {
            return new ValidationResult(false, "目标体重与当前体重差值不应超过50kg，请设置更现实的目标");
        }
        
        return new ValidationResult(true, "目标体重设置合理");
    }
    
    /**
     * 验证健身目标与目标体重的一致性
     * @param currentWeight 当前体重
     * @param targetWeight 目标体重
     * @param fitnessGoal 健身目标
     * @return 验证结果
     */
    public static ValidationResult validateGoalConsistency(double currentWeight, double targetWeight, String fitnessGoal) {
        if (fitnessGoal == null || targetWeight <= 0) {
            return new ValidationResult(true, "目标一致性检查跳过");
        }
        
        boolean isIncreaseGoal = fitnessGoal.contains("增重") || fitnessGoal.contains("增肌");
        boolean isDecreaseGoal = fitnessGoal.contains("减") || fitnessGoal.contains("塑形");
        boolean isMaintainGoal = fitnessGoal.contains("维持");
        
        double weightDiff = targetWeight - currentWeight;
        
        if (isIncreaseGoal && weightDiff < 0) {
            return new ValidationResult(false, 
                String.format("健身目标是\"%s\"，但目标体重(%.1fkg)比当前体重(%.1fkg)低，请调整目标体重", 
                             fitnessGoal, targetWeight, currentWeight));
        }
        
        if (isDecreaseGoal && weightDiff > 0) {
            return new ValidationResult(false, 
                String.format("健身目标是\"%s\"，但目标体重(%.1fkg)比当前体重(%.1fkg)高，请调整目标体重", 
                             fitnessGoal, targetWeight, currentWeight));
        }
        
        if (isMaintainGoal && Math.abs(weightDiff) > 2) {
            return new ValidationResult(false, 
                String.format("健身目标是\"%s\"，但目标体重与当前体重相差%.1fkg，建议目标体重在±2kg范围内", 
                             fitnessGoal, Math.abs(weightDiff)));
        }
        
        return new ValidationResult(true, "健身目标与目标体重一致");
    }
    
    /**
     * 验证姓名是否合理
     * @param name 姓名
     * @return 验证结果
     */
    public static ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "姓名不能为空");
        }
        if (name.trim().length() > 20) {
            return new ValidationResult(false, "姓名长度不能超过20个字符");
        }
        return new ValidationResult(true, "姓名有效");
    }
    
    /**
     * 验证电话号码格式
     * @param phone 电话号码
     * @return 验证结果
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(true, "电话号码为可选项"); // 电话是可选的
        }
        
        // 移除所有非数字字符进行验证
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (cleanPhone.length() < 7 || cleanPhone.length() > 15) {
            return new ValidationResult(false, "电话号码长度应在7-15位数字之间");
        }
        
        return new ValidationResult(true, "电话号码有效");
    }
    
    /**
     * 全面验证用户档案数据
     * @return 验证结果
     */
    public ValidationResult validateProfile() {
        // 验证姓名
        ValidationResult nameResult = validateName(this.name);
        if (!nameResult.isValid()) return nameResult;
        
        // 验证年龄
        ValidationResult ageResult = validateAge(this.age);
        if (!ageResult.isValid()) return ageResult;
        
        // 验证身高
        if (this.height > 0) {
            ValidationResult heightResult = validateHeight(this.height);
            if (!heightResult.isValid()) return heightResult;
        }
        
        // 验证体重
        if (this.weight > 0) {
            ValidationResult weightResult = validateWeight(this.weight);
            if (!weightResult.isValid()) return weightResult;
        }
        
        // 验证目标体重
        if (this.targetWeight > 0 && this.weight > 0) {
            ValidationResult targetResult = validateTargetWeight(this.weight, this.targetWeight);
            if (!targetResult.isValid()) return targetResult;
            
            // 验证健身目标与目标体重的一致性
            ValidationResult consistencyResult = validateGoalConsistency(this.weight, this.targetWeight, this.fitnessGoal);
            if (!consistencyResult.isValid()) return consistencyResult;
        }
        
        // 验证电话
        ValidationResult phoneResult = validatePhone(this.phone);
        if (!phoneResult.isValid()) return phoneResult;
        
        return new ValidationResult(true, "所有数据验证通过");
    }
    
    /**
     * 数据验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
} 