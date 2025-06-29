package model;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * 健康统计数据模型类
 * 用于存储和管理用户的健康统计信息
 * 简化版本 - 只保留实际使用的功能
 */
public class HealthStats {
    private String userName;
    private double currentBMI;
    private String bmiStatus; // 偏瘦/正常/偏胖/肥胖
    private double weightChange; // 体重变化（最近30天）
    private double goalProgress; // 目标达成进度（百分比）
    private double exerciseCompletionRate; // 运动完成率（百分比）
    private double dietRecordFrequency; // 饮食记录频率（最近30天记录天数）
    private String healthScore; // 健康评分（A/B/C/D）
    private List<String> recommendations; // 健康建议
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastUpdated;

    // 构造方法
    public HealthStats() {
        this.recommendations = new ArrayList<>();
        this.lastUpdated = LocalDate.now();
    }

    public HealthStats(String userName) {
        this();
        this.userName = userName;
    }

    // Getter和Setter方法
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public double getCurrentBMI() { return currentBMI; }
    public void setCurrentBMI(double currentBMI) { this.currentBMI = currentBMI; }

    public String getBmiStatus() { return bmiStatus; }
    public void setBmiStatus(String bmiStatus) { this.bmiStatus = bmiStatus; }

    public double getWeightChange() { return weightChange; }
    public void setWeightChange(double weightChange) { this.weightChange = weightChange; }

    public double getGoalProgress() { return goalProgress; }
    public void setGoalProgress(double goalProgress) { this.goalProgress = goalProgress; }

    public double getExerciseCompletionRate() { return exerciseCompletionRate; }
    public void setExerciseCompletionRate(double exerciseCompletionRate) { this.exerciseCompletionRate = exerciseCompletionRate; }

    public double getDietRecordFrequency() { return dietRecordFrequency; }
    public void setDietRecordFrequency(double dietRecordFrequency) { this.dietRecordFrequency = dietRecordFrequency; }

    public String getHealthScore() { return healthScore; }
    public void setHealthScore(String healthScore) { this.healthScore = healthScore; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    /**
     * 添加健康建议
     */
    public void addRecommendation(String recommendation) {
        if (recommendation != null && !recommendation.trim().isEmpty()) {
            this.recommendations.add(recommendation);
        }
    }

    @Override
    public String toString() {
        return String.format("健康统计[用户=%s, BMI=%.1f(%s), 健康评分=%s, 更新时间=%s]",
                userName, currentBMI, bmiStatus, healthScore, lastUpdated);
    }
} 