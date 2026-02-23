package com.studysync.dto;

import com.studysync.model.Reminder;
import java.time.LocalDateTime;

public class ReminderRequest {
    private String title;
    private String message;
    private LocalDateTime reminderTime;
    private Reminder.ReminderType reminderType;
    private Long taskId;
    private Boolean isActive;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public Reminder.ReminderType getReminderType() {
        return reminderType;
    }

    public void setReminderType(Reminder.ReminderType reminderType) {
        this.reminderType = reminderType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
