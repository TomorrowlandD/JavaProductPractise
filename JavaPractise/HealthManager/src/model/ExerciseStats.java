package model;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
// import java.util.List;
// import java.util.ArrayList;

/**
 * 运动统计数据模型类
 * 用于存储和管理用户的运动统计信息
 * 简化版本 - 只保留实际使用的功能
 */
public class ExerciseStats {
    private String userName;
    private int totalPlans;
    private int completedPlans;
    private double completionRate; // 完成率（百分比）
    private Map<String, Integer> exerciseTypeDistribution; // 运动类型分布
    private Map<String, Integer> intensityDistribution; // 强度分布
    private double averageDuration; // 平均运动时长
    private double totalDuration; // 总运动时长
    private int activeDays; // 有运动计划的天数
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastUpdated;

    // 构造方法
    public ExerciseStats() {
        this.exerciseTypeDistribution = new HashMap<>();
        this.intensityDistribution = new HashMap<>();
        this.lastUpdated = LocalDate.now();
    }

    public ExerciseStats(String userName) {
        this();
        this.userName = userName;
    }

    // Getter和Setter方法
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getTotalPlans() { return totalPlans; }
    public void setTotalPlans(int totalPlans) { this.totalPlans = totalPlans; }

    public int getCompletedPlans() { return completedPlans; }
    public void setCompletedPlans(int completedPlans) { this.completedPlans = completedPlans; }

    public double getCompletionRate() { return completionRate; }
    public void setCompletionRate(double completionRate) { this.completionRate = completionRate; }

    public Map<String, Integer> getExerciseTypeDistribution() { return exerciseTypeDistribution; }
    public void setExerciseTypeDistribution(Map<String, Integer> exerciseTypeDistribution) { 
        this.exerciseTypeDistribution = exerciseTypeDistribution; 
    }

    public Map<String, Integer> getIntensityDistribution() { return intensityDistribution; }
    public void setIntensityDistribution(Map<String, Integer> intensityDistribution) { 
        this.intensityDistribution = intensityDistribution; 
    }

    public double getAverageDuration() { return averageDuration; }
    public void setAverageDuration(double averageDuration) { this.averageDuration = averageDuration; }

    public double getTotalDuration() { return totalDuration; }
    public void setTotalDuration(double totalDuration) { this.totalDuration = totalDuration; }

    public int getActiveDays() { return activeDays; }
    public void setActiveDays(int activeDays) { this.activeDays = activeDays; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    /**
     * 添加运动类型统计
     */
    public void addExerciseType(String exerciseType) {
        exerciseTypeDistribution.put(exerciseType, 
            exerciseTypeDistribution.getOrDefault(exerciseType, 0) + 1);
    }

    /**
     * 添加强度统计
     */
    public void addIntensity(String intensity) {
        intensityDistribution.put(intensity, 
            intensityDistribution.getOrDefault(intensity, 0) + 1);
    }

    /**
     * 计算完成率
     */
    public void calculateCompletionRate() {
        if (totalPlans > 0) {
            this.completionRate = (double) completedPlans / totalPlans * 100;
        } else {
            this.completionRate = 0;
        }
    }

    /**
     * 计算平均运动时长
     */
    public void calculateAverageDuration() {
        if (totalPlans > 0) {
            this.averageDuration = totalDuration / totalPlans;
        } else {
            this.averageDuration = 0;
        }
    }

    @Override
    public String toString() {
        return String.format("运动统计[用户=%s, 总计划=%d, 完成率=%.1f%%, 平均时长=%.1f小时, 更新时间=%s]",
                userName, totalPlans, completionRate, averageDuration, lastUpdated);
    }
} 