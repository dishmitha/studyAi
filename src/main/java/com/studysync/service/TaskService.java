package com.studysync.service;

import com.studysync.model.Task;
import com.studysync.model.User;
import com.studysync.model.Subject;
import com.studysync.model.Topic;
import com.studysync.model.StudyPlan;
import com.studysync.repository.TaskRepository;
import com.studysync.repository.UserRepository;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.TopicRepository;
import com.studysync.repository.StudyPlanRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final StudyPlanRepository studyPlanRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       SubjectRepository subjectRepository, TopicRepository topicRepository,
                       StudyPlanRepository studyPlanRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
        this.studyPlanRepository = studyPlanRepository;
    }

    public Task createTask(Task task, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            task.setUser(user.get());

            if (task.getSubject() != null && task.getSubject().getId() != null) {
                Optional<Subject> subject = subjectRepository.findById(task.getSubject().getId());
                subject.ifPresent(task::setSubject);
            }

            if (task.getTopic() != null && task.getTopic().getId() != null) {
                Optional<Topic> topic = topicRepository.findById(task.getTopic().getId());
                topic.ifPresent(task::setTopic);
            }

            if (task.getStudyPlan() != null && task.getStudyPlan().getId() != null) {
                Optional<StudyPlan> studyPlan = studyPlanRepository.findById(task.getStudyPlan().getId());
                studyPlan.ifPresent(task::setStudyPlan);
            }

            return taskRepository.save(task);
        }
        return null;
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> getTasksByDate(Long userId, LocalDate date) {
        return taskRepository.findByUserIdAndTaskDate(userId, date);
    }

    public List<Task> getIncompleteTasks(Long userId) {
        return taskRepository.findByUserIdAndIsCompletedFalse(userId);
    }

    public List<Task> getTasksBySubjectId(Long subjectId) {
        return taskRepository.findBySubjectId(subjectId);
    }

    public List<Task> getMissedTasks(Long userId) {
        return taskRepository.findMissedTasks(userId, LocalDate.now());
    }

    public Task updateTask(Long id, Task task) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task updatedTask = existingTask.get();

            // Handle completion status
            if (task.getIsCompleted() != null && !updatedTask.getIsCompleted() && task.getIsCompleted()) {
                updatedTask.setIsCompleted(true);
                updatedTask.setCompletedAt(LocalDateTime.now());
                
                // Mark topic as completed if linked
                if (updatedTask.getTopic() != null) {
                    Topic topic = updatedTask.getTopic();
                    topic.setIsCompleted(true);
                    topic.setCompletedDate(LocalDate.now());
                    topic.setCompletedHours(topic.getEstimatedHours());
                    topicRepository.save(topic);
                }
            }

            if (task.getTitle() != null) {
                updatedTask.setTitle(task.getTitle());
            }
            if (task.getDescription() != null) {
                updatedTask.setDescription(task.getDescription());
            }
            if (task.getTaskDate() != null) {
                updatedTask.setTaskDate(task.getTaskDate());
            }
            if (task.getStartTime() != null) {
                updatedTask.setStartTime(task.getStartTime());
            }
            if (task.getEndTime() != null) {
                updatedTask.setEndTime(task.getEndTime());
            }
            if (task.getDurationHours() != null) {
                updatedTask.setDurationHours(task.getDurationHours());
            }
            if (task.getTaskType() != null) {
                updatedTask.setTaskType(task.getTaskType());
            }

            return taskRepository.save(updatedTask);
        }
        return null;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public List<Task> rescheduleMissedTasks(Long userId) {
        List<Task> missedTasks = taskRepository.findMissedTasks(userId, LocalDate.now());
        LocalDate newDate = LocalDate.now().plusDays(1);

        for (Task task : missedTasks) {
            task.setTaskDate(newDate);
            taskRepository.save(task);
        }
        return missedTasks;
    }
}
