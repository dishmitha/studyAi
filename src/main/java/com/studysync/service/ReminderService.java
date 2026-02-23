package com.studysync.service;

import com.studysync.model.Reminder;
import com.studysync.model.User;
import com.studysync.model.Task;
import com.studysync.repository.ReminderRepository;
import com.studysync.repository.UserRepository;
import com.studysync.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ReminderService(ReminderRepository reminderRepository, UserRepository userRepository,
                          TaskRepository taskRepository) {
        this.reminderRepository = reminderRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public Reminder createReminder(Reminder reminder, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            reminder.setUser(user.get());

            if (reminder.getTask() != null && reminder.getTask().getId() != null) {
                Optional<Task> task = taskRepository.findById(reminder.getTask().getId());
                task.ifPresent(reminder::setTask);
            }

            return reminderRepository.save(reminder);
        }
        return null;
    }

    public Optional<Reminder> getReminderById(Long id) {
        return reminderRepository.findById(id);
    }

    public List<Reminder> getRemindersByUserId(Long userId) {
        return reminderRepository.findByUserId(userId);
    }

    public List<Reminder> getActiveReminders(Long userId) {
        return reminderRepository.findByUserIdAndIsActiveTrue(userId);
    }

    public List<Reminder> getPendingReminders() {
        return reminderRepository.findPendingReminders(LocalDateTime.now());
    }

    public List<Reminder> getRemindersForTimeRange(Long userId, LocalDateTime start, LocalDateTime end) {
        return reminderRepository.findRemindersForTimeRange(userId, start, end);
    }

    public Reminder updateReminder(Long id, Reminder reminder) {
        Optional<Reminder> existingReminder = reminderRepository.findById(id);
        if (existingReminder.isPresent()) {
            Reminder updatedReminder = existingReminder.get();
            
            if (reminder.getTitle() != null) {
                updatedReminder.setTitle(reminder.getTitle());
            }
            if (reminder.getMessage() != null) {
                updatedReminder.setMessage(reminder.getMessage());
            }
            if (reminder.getReminderTime() != null) {
                updatedReminder.setReminderTime(reminder.getReminderTime());
            }
            if (reminder.getReminderType() != null) {
                updatedReminder.setReminderType(reminder.getReminderType());
            }
            if (reminder.getIsActive() != null) {
                updatedReminder.setIsActive(reminder.getIsActive());
            }
            
            return reminderRepository.save(updatedReminder);
        }
        return null;
    }

    public void deleteReminder(Long id) {
        reminderRepository.deleteById(id);
    }

    public List<Reminder> createDailyReminders(Long userId) {
        // Create a daily reminder at 8 AM
        Reminder dailyReminder = new Reminder();
        dailyReminder.setTitle("Daily Study Reminder");
        dailyReminder.setMessage("Time to start your study session! Check your today's tasks.");
        dailyReminder.setReminderTime(LocalDateTime.now().withHour(8).withMinute(0).withSecond(0));
        dailyReminder.setReminderType(Reminder.ReminderType.DAILY);
        dailyReminder.setIsActive(true);
        
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            dailyReminder.setUser(user.get());
            reminderRepository.save(dailyReminder);
        }
        
        return reminderRepository.findByUserId(userId);
    }
}
