package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 运动计划数据模型类
 * 用于存储和管理用户的运动计划信息
 */
public class ExercisePlan {
    private int id;
    private String userName;
    private String exerciseType;
    private LocalDate planDate;
    private Double duration;
    private String intensity;
    private boolean isCompleted;
    private Double actualDuration;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 预设的运动类型
    public static final String[] EXERCISE_TYPES = {
            "跑步", "游泳", "健身", "瑜伽", "骑行", "步行",
            "篮球", "足球", "羽毛球", "网球", "跳绳", "其他"
    };

    // 预设的运动强度
    public static final String[] INTENSITY_LEVELS = { "低", "中", "高" };

    // 构造方法
    public ExercisePlan() {
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ExercisePlan(String userName, String exerciseType, LocalDate planDate) {
        this();
        this.userName = userName;
        this.exerciseType = exerciseType;
        this.planDate = planDate;
    }

    // Getter和Setter方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public LocalDate getPlanDate() {
        return planDate;
    }

    public void setPlanDate(LocalDate planDate) {
        this.planDate = planDate;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Double getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(Double actualDuration) {
        this.actualDuration = actualDuration;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 获取完成状态的中文描述
     */
    public String getCompletionStatusText() {
        return isCompleted ? "已完成" : "未完成";
    }

    /**
     * 获取计划日期的格式化字符串
     */
    public String getPlanDateString() {
        return planDate != null ? planDate.toString() : "";
    }

    /**
     * 获取时长的格式化字符串
     */
    public String getDurationString() {
        if (duration == null)
            return "";
        return String.format("%.1f小时", duration);
    }

    /**
     * 获取实际时长的格式化字符串
     */
    public String getActualDurationString() {
        if (actualDuration == null)
            return "";
        return String.format("%.1f小时", actualDuration);
    }

    /**
     * 验证运动计划数据
     */
    public ValidationResult validatePlan() {
        if (userName == null || userName.trim().isEmpty()) {
            return new ValidationResult(false, "用户名不能为空");
        }

        if (exerciseType == null || exerciseType.trim().isEmpty()) {
            return new ValidationResult(false, "运动类型不能为空");
        }

        if (planDate == null) {
            return new ValidationResult(false, "计划日期不能为空");
        }

        if (planDate.isBefore(LocalDate.now())) {
            return new ValidationResult(false, "计划日期不能早于今天");
        }

        if (duration != null && (duration <= 0 || duration > 24)) {
            return new ValidationResult(false, "计划时长必须在0-24小时之间");
        }

        if (actualDuration != null && (actualDuration < 0 || actualDuration > 24)) {
            return new ValidationResult(false, "实际时长必须在0-24小时之间");
        }

        return new ValidationResult(true, "数据验证通过");
    }

    /**
     * 检查计划是否过期（计划日期已过但未完成）
     */
    public boolean isOverdue() {
        return planDate.isBefore(LocalDate.now()) && !isCompleted;
    }

    /**
     * 获取计划状态描述
     */
    public String getStatusDescription() {
        if (isCompleted) {
            return "已完成";
        } else if (isOverdue()) {
            return "已过期";
        } else if (planDate.equals(LocalDate.now())) {
            return "今日计划";
        } else if (planDate.isAfter(LocalDate.now())) {
            return "未来计划";
        } else {
            return "未知状态";
        }
    }

    @Override
    public String toString() {
        return String.format("运动计划[ID=%d, 用户=%s, 类型=%s, 日期=%s, 完成=%s]",
                id, userName, exerciseType, planDate, isCompleted ? "是" : "否");
    }

    /**
     * 验证结果内部类
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