package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 饮食记录数据模型类
 * 用于存储和管理用户的饮食记录信息
 */
public class DietRecord {
    private int id;
    private String userName;
    private LocalDate recordDate;
    private String breakfast;
    private String lunch;
    private String dinner;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 构造方法
    public DietRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public DietRecord(String userName, LocalDate recordDate) {
        this();
        this.userName = userName;
        this.recordDate = recordDate;
    }

    // Getter和Setter方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public String getBreakfast() { return breakfast; }
    public void setBreakfast(String breakfast) { this.breakfast = breakfast; }

    public String getLunch() { return lunch; }
    public void setLunch(String lunch) { this.lunch = lunch; }

    public String getDinner() { return dinner; }
    public void setDinner(String dinner) { this.dinner = dinner; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * 获取记录日期的格式化字符串
     */
    public String getRecordDateString() {
        return recordDate != null ? recordDate.toString() : "";
    }

    /**
     * 验证饮食记录数据
     */
    public ValidationResult validateRecord() {
        if (userName == null || userName.trim().isEmpty()) {
            return new ValidationResult(false, "用户名不能为空");
        }
        if (recordDate == null) {
            return new ValidationResult(false, "记录日期不能为空");
        }
        if (recordDate.isAfter(LocalDate.now())) {
            return new ValidationResult(false, "记录日期不能晚于今天");
        }
        // 至少需要记录一餐
        if ((breakfast == null || breakfast.trim().isEmpty()) &&
            (lunch == null || lunch.trim().isEmpty()) &&
            (dinner == null || dinner.trim().isEmpty())) {
            return new ValidationResult(false, "至少需要记录一餐内容");
        }
        return new ValidationResult(true, "数据验证通过");
    }

    /**
     * 获取饮食记录摘要
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        if (breakfast != null && !breakfast.trim().isEmpty()) {
            summary.append("早餐: ").append(breakfast);
        }
        if (lunch != null && !lunch.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("午餐: ").append(lunch);
        }
        if (dinner != null && !dinner.trim().isEmpty()) {
            if (summary.length() > 0) summary.append(" | ");
            summary.append("晚餐: ").append(dinner);
        }
        return summary.toString();
    }

    @Override
    public String toString() {
        return String.format("饮食记录[ID=%d, 用户=%s, 日期=%s, 摘要=%s]",
                id, userName, recordDate, getSummary());
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
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
} 