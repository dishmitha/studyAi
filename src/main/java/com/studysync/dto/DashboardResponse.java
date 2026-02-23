package com.studysync.dto;

import java.util.List;

public class DashboardResponse {
    private String userName;
    private Integer dailyStudyHours;
    private Double totalHoursStudied;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer totalSubjects;
    private Integer completedSubjects;
    private Integer totalTasks;
    private Integer completedTasks;
    private Integer pendingTasks;
    private Double todayStudyHours;
    private List<TaskDto> todayTasks;
    private List<SubjectDto> subjects;
    private List<ReminderDto> upcomingReminders;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getDailyStudyHours() { return dailyStudyHours; }
    public void setDailyStudyHours(Integer dailyStudyHours) { this.dailyStudyHours = dailyStudyHours; }

    public Double getTotalHoursStudied() { return totalHoursStudied; }
    public void setTotalHoursStudied(Double totalHoursStudied) { this.totalHoursStudied = totalHoursStudied; }

    public Integer getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(Integer currentStreak) { this.currentStreak = currentStreak; }

    public Integer getLongestStreak() { return longestStreak; }
    public void setLongestStreak(Integer longestStreak) { this.longestStreak = longestStreak; }

    public Integer getTotalSubjects() { return totalSubjects; }
    public void setTotalSubjects(Integer totalSubjects) { this.totalSubjects = totalSubjects; }

    public Integer getCompletedSubjects() { return completedSubjects; }
    public void setCompletedSubjects(Integer completedSubjects) { this.completedSubjects = completedSubjects; }

    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }

    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }

    public Integer getPendingTasks() { return pendingTasks; }
    public void setPendingTasks(Integer pendingTasks) { this.pendingTasks = pendingTasks; }

    public Double getTodayStudyHours() { return todayStudyHours; }
    public void setTodayStudyHours(Double todayStudyHours) { this.todayStudyHours = todayStudyHours; }

    public List<TaskDto> getTodayTasks() { return todayTasks; }
    public void setTodayTasks(List<TaskDto> todayTasks) { this.todayTasks = todayTasks; }

    public List<SubjectDto> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectDto> subjects) { this.subjects = subjects; }

    public List<ReminderDto> getUpcomingReminders() { return upcomingReminders; }
    public void setUpcomingReminders(List<ReminderDto> upcomingReminders) { this.upcomingReminders = upcomingReminders; }

    public static class TaskDto {
        private Long id;
        private String title;
        private String subjectName;
        private Boolean isCompleted;
        private Double estimatedHours;
        private String scheduledDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
        public Boolean getIsCompleted() { return isCompleted; }
        public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
        public Double getEstimatedHours() { return estimatedHours; }
        public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }
        public String getScheduledDate() { return scheduledDate; }
        public void setScheduledDate(String scheduledDate) { this.scheduledDate = scheduledDate; }
    }

    public static class SubjectDto {
        private Long id;
        private String name;
        private String priority;
        private Integer totalTopics;
        private Integer completedTopics;
        private String examDate;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public Integer getTotalTopics() { return totalTopics; }
        public void setTotalTopics(Integer totalTopics) { this.totalTopics = totalTopics; }
        public Integer getCompletedTopics() { return completedTopics; }
        public void setCompletedTopics(Integer completedTopics) { this.completedTopics = completedTopics; }
        public String getExamDate() { return examDate; }
        public void setExamDate(String examDate) { this.examDate = examDate; }
    }

    public static class ReminderDto {
        private Long id;
        private String title;
        private String reminderDate;
        private String type;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getReminderDate() { return reminderDate; }
        public void setReminderDate(String reminderDate) { this.reminderDate = reminderDate; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
