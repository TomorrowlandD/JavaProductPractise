package model;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
/**
 * 饮食统计数据模型类
 * 用于存储和管理用户的饮食统计信息
 * 简化版本 - 只保留实际使用的功能
 */
public class DietStats {
    private String userName;
    private int totalRecords;
    private int daysWithRecords;
    private double recordFrequency; // 记录频率（百分比）
    private Map<String, Integer> mealCompletion; // 三餐完成情况
    private int consecutiveDays; // 连续记录天数
    private double averageMealsPerDay; // 平均每日记录餐数
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastUpdated;
    private Map<String, Integer> foodPreference; // 食物偏好统计

    // 构造方法
    public DietStats() {
        this.mealCompletion = new HashMap<>();
        this.foodPreference = new HashMap<>();
        this.lastUpdated = LocalDate.now();
        
        // 初始化三餐完成情况
        this.mealCompletion.put("早餐", 0);
        this.mealCompletion.put("午餐", 0);
        this.mealCompletion.put("晚餐", 0);
    }

    public DietStats(String userName) {
        this();
        this.userName = userName;
    }

    // Getter和Setter方法
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getTotalRecords() { return totalRecords; }
    public void setTotalRecords(int totalRecords) { this.totalRecords = totalRecords; }

    public int getDaysWithRecords() { return daysWithRecords; }
    public void setDaysWithRecords(int daysWithRecords) { this.daysWithRecords = daysWithRecords; }

    public double getRecordFrequency() { return recordFrequency; }
    public void setRecordFrequency(double recordFrequency) { this.recordFrequency = recordFrequency; }

    public Map<String, Integer> getMealCompletion() { return mealCompletion; }
    public void setMealCompletion(Map<String, Integer> mealCompletion) { this.mealCompletion = mealCompletion; }

    public int getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(int consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public double getAverageMealsPerDay() { return averageMealsPerDay; }
    public void setAverageMealsPerDay(double averageMealsPerDay) { this.averageMealsPerDay = averageMealsPerDay; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    public Map<String, Integer> getFoodPreference() { return foodPreference; }
    public void setFoodPreference(Map<String, Integer> foodPreference) { this.foodPreference = foodPreference; }

    /**
     * 添加餐次完成统计
     */
    public void addMealCompletion(String mealType) {
        mealCompletion.put(mealType, mealCompletion.getOrDefault(mealType, 0) + 1);
    }

    /**
     * 添加食物偏好统计
     */
    public void addFoodPreference(String food) {
        foodPreference.put(food, foodPreference.getOrDefault(food, 0) + 1);
    }

    /**
     * 计算记录频率
     */
    public void calculateRecordFrequency(int totalDays) {
        if (totalDays > 0) {
            this.recordFrequency = (double) daysWithRecords / totalDays * 100;
        } else {
            this.recordFrequency = 0;
        }
    }

    /**
     * 计算平均每日餐数
     */
    public void calculateAverageMealsPerDay() {
        if (daysWithRecords > 0) {
            this.averageMealsPerDay = (double) totalRecords / daysWithRecords;
        } else {
            this.averageMealsPerDay = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("饮食统计[用户=%s, 总记录=%d, 记录频率=%.1f%%, 平均餐数=%.1f, 更新时间=%s]",
                userName, totalRecords, recordFrequency, averageMealsPerDay, lastUpdated);
    }
} 