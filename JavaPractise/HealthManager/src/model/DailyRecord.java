package model;

import java.io.Serializable;
import java.time.LocalDate;

public class DailyRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String userName;
    private LocalDate date;
    private double weight;
    private String exercise;
    private double exerciseDuration; // 单位：小时
    private double sleepDuration;    // 单位：小时
    private String mood;
    private String note;

    public DailyRecord() {
        this.date = LocalDate.now();
    }

    public DailyRecord(String userName, LocalDate date, double weight, String exercise, double exerciseDuration, double sleepDuration, String mood, String note) {
        this.userName = userName;
        this.date = date;
        this.weight = weight;
        this.exercise = exercise;
        this.exerciseDuration = exerciseDuration;
        this.sleepDuration = sleepDuration;
        this.mood = mood;
        this.note = note;
    }

    // getter和setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getExercise() { return exercise; }
    public void setExercise(String exercise) { this.exercise = exercise; }
    public double getExerciseDuration() { return exerciseDuration; }
    public void setExerciseDuration(double exerciseDuration) { this.exerciseDuration = exerciseDuration; }
    public double getSleepDuration() { return sleepDuration; }
    public void setSleepDuration(double sleepDuration) { this.sleepDuration = sleepDuration; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    // ==================== 数据校验方法 ====================
    
    /**
     * 验证运动时长是否在合理范围内
     * @param duration 运动时长（小时）
     * @return 验证结果
     */
    public static ValidationResult validateExerciseDuration(double duration) {
        if (duration < 0) {
            return new ValidationResult(false, "运动时长不能为负数");
        }
        if (duration > 24) {
            return new ValidationResult(false, "运动时长不能超过24小时");
        }
        return new ValidationResult(true, "运动时长有效");
    }
    
    /**
     * 验证睡眠时长是否在合理范围内
     * @param duration 睡眠时长（小时）
     * @return 验证结果
     */
    public static ValidationResult validateSleepDuration(double duration) {
        if (duration < 0) {
            return new ValidationResult(false, "睡眠时长不能为负数");
        }
        if (duration > 24) {
            return new ValidationResult(false, "睡眠时长不能超过24小时");
        }
        return new ValidationResult(true, "睡眠时长有效");
    }
    
    /**
     * 验证日期是否在合理范围内
     * @param date 日期
     * @return 验证结果
     */
    public static ValidationResult validateDate(LocalDate date) {
        if (date == null) {
            return new ValidationResult(false, "日期不能为空");
        }
        
        LocalDate minDate = LocalDate.of(2020, 1, 1);
        LocalDate maxDate = LocalDate.now();
        
        if (date.isBefore(minDate)) {
            return new ValidationResult(false, "日期不能早于2020-01-01");
        }
        if (date.isAfter(maxDate)) {
            return new ValidationResult(false, "不能记录未来的健康数据");
        }
        return new ValidationResult(true, "日期有效");
    }
    
    /**
     * 验证体重是否在合理范围内
     * @param weight 体重（kg）
     * @return 验证结果
     */
    public static ValidationResult validateWeight(double weight) {
        if (weight <= 0) {
            return new ValidationResult(false, "体重必须大于0");
        }
        if (weight > 500) {
            return new ValidationResult(false, "体重不能超过500kg");
        }
        return new ValidationResult(true, "体重有效");
    }
    
    /**
     * 验证用户名是否有效
     * @param userName 用户名
     * @return 验证结果
     */
    public static ValidationResult validateUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            return new ValidationResult(false, "用户名不能为空");
        }
        if (userName.trim().length() > 50) {
            return new ValidationResult(false, "用户名长度不能超过50个字符");
        }
        return new ValidationResult(true, "用户名有效");
    }
    
    /**
     * 全面验证每日记录数据
     * @return 验证结果
     */
    public ValidationResult validateRecord() {
        // 验证用户名
        ValidationResult userNameResult = validateUserName(this.userName);
        if (!userNameResult.isValid()) return userNameResult;
        
        // 验证日期
        ValidationResult dateResult = validateDate(this.date);
        if (!dateResult.isValid()) return dateResult;
        
        // 验证体重
        ValidationResult weightResult = validateWeight(this.weight);
        if (!weightResult.isValid()) return weightResult;
        
        // 验证运动时长
        ValidationResult exerciseResult = validateExerciseDuration(this.exerciseDuration);
        if (!exerciseResult.isValid()) return exerciseResult;
        
        // 验证睡眠时长
        ValidationResult sleepResult = validateSleepDuration(this.sleepDuration);
        if (!sleepResult.isValid()) return sleepResult;
        
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
} 