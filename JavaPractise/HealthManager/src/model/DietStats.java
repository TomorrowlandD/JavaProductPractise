package model;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * 饮食统计数据模型类
 * 用于存储和管理用户的饮食统计信息
 */
public class DietStats {
    private String userName;
    private int totalRecords;
    private int daysWithRecords;
    private double recordFrequency; // 记录频率（百分比）
    private Map<String, Integer> mealCompletion; // 三餐完成情况
    private List<String> commonFoods; // 常见食物
    private int consecutiveDays; // 连续记录天数
    private double averageMealsPerDay; // 平均每日记录餐数
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastUpdated;
    private Map<String, Integer> foodPreference; // 食物偏好统计

    // 构造方法
    public DietStats() {
        this.mealCompletion = new HashMap<>();
        this.commonFoods = new ArrayList<>();
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

    public List<String> getCommonFoods() { return commonFoods; }
    public void setCommonFoods(List<String> commonFoods) { this.commonFoods = commonFoods; }

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

    /**
     * 获取记录频率描述
     */
    public String getRecordFrequencyDescription() {
        if (recordFrequency >= 90) {
            return "优秀 - 饮食记录非常规律！";
        } else if (recordFrequency >= 70) {
            return "良好 - 饮食记录比较规律";
        } else if (recordFrequency >= 50) {
            return "一般 - 饮食记录有待改善";
        } else {
            return "较差 - 需要加强饮食记录习惯";
        }
    }

    /**
     * 获取最常记录的餐次
     */
    public String getMostRecordedMeal() {
        String mostRecorded = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : mealCompletion.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostRecorded = entry.getKey();
            }
        }
        return mostRecorded != null ? mostRecorded : "暂无记录";
    }

    /**
     * 获取最常吃的食物
     */
    public String getMostPreferredFood() {
        if (foodPreference.isEmpty()) {
            return "暂无食物偏好数据";
        }
        
        String mostPreferred = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : foodPreference.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPreferred = entry.getKey();
            }
        }
        return mostPreferred;
    }

    /**
     * 获取三餐完成情况描述
     */
    public String getMealCompletionDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("三餐完成情况：");
        
        for (Map.Entry<String, Integer> entry : mealCompletion.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("次");
            if (!entry.getKey().equals("晚餐")) {
                sb.append("，");
            }
        }
        
        return sb.toString();
    }

    /**
     * 获取饮食建议
     */
    public List<String> getDietRecommendations() {
        List<String> recommendations = new ArrayList<>();
        
        if (recordFrequency < 70) {
            recommendations.add("建议提高饮食记录的频率，养成每日记录的习惯");
        }
        
        if (averageMealsPerDay < 2) {
            recommendations.add("建议记录更多餐次，包括早餐、午餐、晚餐");
        }
        
        if (consecutiveDays < 7) {
            recommendations.add("建议连续记录一周以上，更好地了解饮食规律");
        }
        
        // 检查食物多样性
        if (foodPreference.size() < 5) {
            recommendations.add("建议增加食物种类，保持营养均衡");
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("饮食记录习惯良好，请继续保持！");
        }
        
        return recommendations;
    }

    /**
     * 获取饮食规律性评分
     */
    public String getDietRegularityScore() {
        double score = 0;
        
        // 记录频率权重40%
        score += recordFrequency * 0.4;
        
        // 平均餐数权重30%
        score += Math.min(averageMealsPerDay / 3.0 * 100, 100) * 0.3;
        
        // 连续天数权重30%
        score += Math.min(consecutiveDays / 30.0 * 100, 100) * 0.3;
        
        if (score >= 90) return "A";
        else if (score >= 80) return "B";
        else if (score >= 70) return "C";
        else return "D";
    }

    @Override
    public String toString() {
        return String.format("饮食统计[用户=%s, 总记录=%d, 记录频率=%.1f%%, 平均餐数=%.1f, 更新时间=%s]",
                userName, totalRecords, recordFrequency, averageMealsPerDay, lastUpdated);
    }
} 