package com.studysync.service;

import com.studysync.model.Progress;
import com.studysync.model.User;
import com.studysync.model.Task;
import com.studysync.repository.ProgressRepository;
import com.studysync.repository.UserRepository;
import com.studysync.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public ProgressService(ProgressRepository progressRepository, UserRepository userRepository,
                           TaskRepository taskRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public Progress createOrUpdateDailyProgress(Long userId, LocalDate date) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return null;
        }

        Optional<Progress> existingProgress = progressRepository.findByUserIdAndProgressDate(userId, date);
        Progress progress;

        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            progress = new Progress();
            progress.setUser(user.get());
            progress.setProgressDate(date);
        }

        // Get tasks for the day
        List<Task> tasks = taskRepository.findByUserIdAndTaskDate(userId, date);
        
        int totalTasks = tasks.size();
        long completedTasks = tasks.stream().filter(Task::getIsCompleted).count();
        double studyHours = tasks.stream()
                .filter(t -> t.getIsCompleted() && t.getDurationHours() != null)
                .mapToDouble(Task::getDurationHours)
                .sum();

        progress.setTotalTasks(totalTasks);
        progress.setCompletedTasks((int) completedTasks);
        progress.setStudyHours(studyHours);
        
        double completionPercentage = totalTasks > 0 ? 
            (completedTasks * 100.0 / totalTasks) : 0.0;
        progress.setCompletionPercentage(completionPercentage);

        return progressRepository.save(progress);
    }

    public Optional<Progress> getProgressById(Long id) {
        return progressRepository.findById(id);
    }

    public List<Progress> getProgressByUserId(Long userId) {
        return progressRepository.findByUserIdOrderByProgressDateDesc(userId);
    }

    public Optional<Progress> getProgressForDate(Long userId, LocalDate date) {
        return progressRepository.findByUserIdAndProgressDate(userId, date);
    }

    public List<Progress> getProgressBetweenDates(Long userId, LocalDate startDate, LocalDate endDate) {
        return progressRepository.findByUserIdAndProgressDateBetween(userId, startDate, endDate);
    }

    public int calculateStudyStreak(Long userId) {
        List<Progress> progressList = progressRepository.findByUserIdOrderByProgressDateDesc(userId);
        int streak = 0;
        
        for (Progress progress : progressList) {
            if (progress.getCompletionPercentage() != null && progress.getCompletionPercentage() >= 50) {
                streak++;
            } else if (progress.getCompletionPercentage() != null && progress.getCompletionPercentage() > 0) {
                // Break in streak
                break;
            }
        }
        
        return streak;
    }

    public Double getTotalStudyHours(Long userId) {
        return progressRepository.totalStudyHours(userId);
    }

    public Integer getActiveStudyDays(Long userId) {
        return progressRepository.countActiveDays(userId);
    }

    public Progress getOverallProgress(Long userId) {
        List<Progress> progressList = progressRepository.findByUserId(userId);
        
        if (progressList.isEmpty()) {
            return null;
        }

        Progress overallProgress = new Progress();
        overallProgress.setUser(progressList.get(0).getUser());
        
        int totalTasks = progressList.stream().mapToInt(Progress::getTotalTasks).sum();
        int completedTasks = progressList.stream().mapToInt(Progress::getCompletedTasks).sum();
        double totalHours = progressList.stream().mapToDouble(Progress::getStudyHours).sum();
        
        overallProgress.setTotalTasks(totalTasks);
        overallProgress.setCompletedTasks(completedTasks);
        overallProgress.setStudyHours(totalHours);
        
        double overallPercentage = totalTasks > 0 ? 
            (completedTasks * 100.0 / totalTasks) : 0.0;
        overallProgress.setCompletionPercentage(overallPercentage);
        
        return overallProgress;
    }
}
