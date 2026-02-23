package com.studysync.service;

import com.studysync.model.StudyPlan;
import com.studysync.model.User;
import com.studysync.model.Subject;
import com.studysync.model.Topic;
import com.studysync.model.Task;
import com.studysync.repository.StudyPlanRepository;
import com.studysync.repository.UserRepository;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.TopicRepository;
import com.studysync.repository.TaskRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final TaskRepository taskRepository;

    public StudyPlanService(StudyPlanRepository studyPlanRepository, UserRepository userRepository,
                           SubjectRepository subjectRepository, TopicRepository topicRepository,
                           TaskRepository taskRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.taskRepository = taskRepository;
    }

    public StudyPlan createStudyPlan(StudyPlan studyPlan, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            studyPlan.setUser(user.get());
            return studyPlanRepository.save(studyPlan);
        }
        return null;
    }

    public Optional<StudyPlan> getStudyPlanById(Long id) {
        return studyPlanRepository.findById(id);
    }

    public List<StudyPlan> getStudyPlansByUserId(Long userId) {
        return studyPlanRepository.findByUserId(userId);
    }

    public Optional<StudyPlan> getActiveStudyPlan(Long userId) {
        return studyPlanRepository.findByUserIdAndIsActiveTrue(userId);
    }

    public StudyPlan generateAutoTimetable(Long userId, LocalDate startDate, LocalDate endDate) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return null;
        }

        User user = userOpt.get();
        Integer dailyHours = user.getDailyStudyHours() != null ? user.getDailyStudyHours() : 2;

        // Deactivate any existing active plans
        Optional<StudyPlan> existingPlan = studyPlanRepository.findByUserIdAndIsActiveTrue(userId);
        if (existingPlan.isPresent()) {
            StudyPlan plan = existingPlan.get();
            plan.setIsActive(false);
            studyPlanRepository.save(plan);
        }

        // Create new study plan
        StudyPlan studyPlan = new StudyPlan();
        studyPlan.setPlanName("Auto-generated Study Plan");
        studyPlan.setStartDate(startDate);
        studyPlan.setEndDate(endDate);
        studyPlan.setUser(user);
        studyPlan.setIsActive(true);
        studyPlan = studyPlanRepository.save(studyPlan);

        // Get all subjects for the user
        List<Subject> subjects = subjectRepository.findByUserIdOrderByPriorityDesc(userId);
        
        if (subjects.isEmpty()) {
            return studyPlan;
        }

        // Calculate total available days and hours
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double totalAvailableHours = totalDays * dailyHours;

        // Get all incomplete topics
        List<Topic> allTopics = topicRepository.findByIsCompletedFalseOrderByScheduledDateAsc();
        allTopics.removeIf(t -> !t.getSubject().getUser().getId().equals(userId));

        // Distribute topics across days
        LocalDate currentDate = startDate;
        double hoursForToday = 0;

        for (Topic topic : allTopics) {
            if (topic.getScheduledDate() == null) {
                // Schedule topic
                if (hoursForToday >= dailyHours) {
                    currentDate = currentDate.plusDays(1);
                    hoursForToday = 0;
                }

                if (currentDate.isAfter(endDate)) {
                    // Extend the plan if needed
                    endDate = currentDate;
                    studyPlan.setEndDate(endDate);
                }

                topic.setScheduledDate(currentDate);
                topicRepository.save(topic);

                // Create a task for this topic
                Task task = new Task();
                task.setTitle("Study: " + topic.getName());
                task.setDescription(topic.getDescription());
                task.setTaskDate(currentDate);
                task.setStartTime("09:00");
                task.setDurationHours(topic.getEstimatedHours());
                task.setTaskType(Task.TaskType.STUDY);
                task.setSubject(topic.getSubject());
                task.setTopic(topic);
                task.setUser(user);
                task.setStudyPlan(studyPlan);
                taskRepository.save(task);

                hoursForToday += topic.getEstimatedHours();
                studyPlan.setTotalHours(studyPlan.getTotalHours() + topic.getEstimatedHours());
            }
        }

        return studyPlanRepository.save(studyPlan);
    }

    public StudyPlan updateStudyPlan(Long id, StudyPlan studyPlan) {
        Optional<StudyPlan> existingPlan = studyPlanRepository.findById(id);
        if (existingPlan.isPresent()) {
            StudyPlan updatedPlan = existingPlan.get();
            if (studyPlan.getPlanName() != null) {
                updatedPlan.setPlanName(studyPlan.getPlanName());
            }
            if (studyPlan.getStartDate() != null) {
                updatedPlan.setStartDate(studyPlan.getStartDate());
            }
            if (studyPlan.getEndDate() != null) {
                updatedPlan.setEndDate(studyPlan.getEndDate());
            }
            if (studyPlan.getTotalHours() != null) {
                updatedPlan.setTotalHours(studyPlan.getTotalHours());
            }
            if (studyPlan.getCompletedHours() != null) {
                updatedPlan.setCompletedHours(studyPlan.getCompletedHours());
            }
            if (studyPlan.getIsActive() != null) {
                updatedPlan.setIsActive(studyPlan.getIsActive());
            }
            return studyPlanRepository.save(updatedPlan);
        }
        return null;
    }

    public void deleteStudyPlan(Long id) {
        studyPlanRepository.deleteById(id);
    }
}
