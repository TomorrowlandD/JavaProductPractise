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
} 