package com.studysync.service;

import com.studysync.dto.DashboardResponse;
import com.studysync.model.*;
import com.studysync.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final TaskRepository taskRepository;
    private final ProgressRepository progressRepository;
    private final ReminderRepository reminderRepository;

    public DashboardService(UserRepository userRepository, SubjectRepository subjectRepository,
                           TopicRepository topicRepository, TaskRepository taskRepository,
                           ProgressRepository progressRepository, ReminderRepository reminderRepository) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.taskRepository = taskRepository;
        this.progressRepository = progressRepository;
        this.reminderRepository = reminderRepository;
    }

    public DashboardResponse getDashboard(Long userId) {
        DashboardResponse response = new DashboardResponse();

        // Get user info
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            response.setUserName(user.getName());
            response.setDailyStudyHours(user.getDailyStudyHours());
        }

        // Get subject stats
        List<Subject> subjects = subjectRepository.findByUserId(userId);
        response.setTotalSubjects(subjects.size());
        int completedSubjects = 0;
        for (Subject s : subjects) {
            if (s.getCompletedTopics() != null && s.getTotalTopics() != null 
                && s.getCompletedTopics() >= s.getTotalTopics()) {
                completedSubjects++;
            }
        }
        response.setCompletedSubjects(completedSubjects);

        // Get task stats
        List<Task> allTasks = taskRepository.findByUserId(userId);
        response.setTotalTasks(allTasks.size());
        int completedTasks = 0;
        int pendingTasks = 0;
        double todayHours = 0.0;
        List<DashboardResponse.TaskDto> todayTasks = new ArrayList<>();

        LocalDate today = LocalDate.now();
        for (Task task : allTasks) {
            if (task.getIsCompleted() != null && task.getIsCompleted()) {
                completedTasks++;
            } else {
                pendingTasks++;
            }
            
            // Today's tasks - use taskDate instead of scheduledDate
            if (task.getTaskDate() != null && task.getTaskDate().equals(today)) {
                DashboardResponse.TaskDto taskDto = new DashboardResponse.TaskDto();
                taskDto.setId(task.getId());
                taskDto.setTitle(task.getTitle());
                taskDto.setIsCompleted(task.getIsCompleted());
                // Use durationHours instead of estimatedHours
                taskDto.setEstimatedHours(task.getDurationHours());
                // Use taskDate instead of scheduledDate
                taskDto.setScheduledDate(task.getTaskDate() != null ? task.getTaskDate().toString() : null);
                if (task.getSubject() != null) {
                    taskDto.setSubjectName(task.getSubject().getName());
                }
                todayTasks.add(taskDto);
                
                if (task.getIsCompleted() != null && task.getIsCompleted()) {
                    todayHours += task.getDurationHours() != null ? task.getDurationHours() : 0;
                }
            }
        }
        response.setCompletedTasks(completedTasks);
        response.setPendingTasks(pendingTasks);
        response.setTodayStudyHours(todayHours);
        response.setTodayTasks(todayTasks);

        // Get progress stats - use progressDate instead of studyDate
        List<Progress> progressList = progressRepository.findByUserId(userId);
        double totalHours = 0.0;
        int currentStreak = 0;
        int longestStreak = 0;
        
        for (Progress p : progressList) {
            if (p.getStudyHours() != null) {
                totalHours += p.getStudyHours();
            }
            if (p.getStreakDays() != null) {
                if (p.getStreakDays() > longestStreak) {
                    longestStreak = p.getStreakDays();
                }
            }
        }
        
        // Calculate current streak from today's progress
        for (Progress p : progressList) {
            // Use progressDate instead of studyDate
            if (p.getProgressDate() != null && p.getProgressDate().equals(today)) {
                currentStreak = p.getStreakDays() != null ? p.getStreakDays() : 0;
                break;
            }
        }
        
        response.setTotalHoursStudied(totalHours);
        response.setCurrentStreak(currentStreak);
        response.setLongestStreak(longestStreak);

        // Get subject details
        List<DashboardResponse.SubjectDto> subjectDtos = new ArrayList<>();
        for (Subject s : subjects) {
            DashboardResponse.SubjectDto dto = new DashboardResponse.SubjectDto();
            dto.setId(s.getId());
            dto.setName(s.getName());
            dto.setPriority(s.getPriority() != null ? s.getPriority().name() : "MEDIUM");
            dto.setTotalTopics(s.getTotalTopics());
            dto.setCompletedTopics(s.getCompletedTopics());
            if (s.getExamDate() != null) {
                dto.setExamDate(s.getExamDate().toString());
            }
            subjectDtos.add(dto);
        }
        response.setSubjects(subjectDtos);

        // Get upcoming reminders - use reminderTime instead of reminderDate, reminderType instead of type
        List<Reminder> reminders = reminderRepository.findByUserId(userId);
        List<DashboardResponse.ReminderDto> reminderDtos = new ArrayList<>();
        for (Reminder r : reminders) {
            if (r.getReminderTime() != null && r.getReminderTime().toLocalDate().isAfter(today)) {
                DashboardResponse.ReminderDto dto = new DashboardResponse.ReminderDto();
                dto.setId(r.getId());
                dto.setTitle(r.getTitle());
                // Use reminderTime instead of reminderDate
                dto.setReminderDate(r.getReminderTime() != null ? r.getReminderTime().toLocalDate().toString() : null);
                // Use reminderType instead of type
                dto.setType(r.getReminderType() != null ? r.getReminderType().name() : "DAILY");
                reminderDtos.add(dto);
            }
        }
        response.setUpcomingReminders(reminderDtos);

        return response;
    }
}
