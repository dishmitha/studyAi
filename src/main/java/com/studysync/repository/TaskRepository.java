package com.studysync.repository;

import com.studysync.model.Task;
import com.studysync.model.User;
import com.studysync.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserId(Long userId);
    List<Task> findByTaskDate(LocalDate taskDate);
    List<Task> findByUserIdAndTaskDate(Long userId, LocalDate taskDate);
    List<Task> findByUserIdAndIsCompletedFalse(Long userId);
    List<Task> findBySubject(Subject subject);
    List<Task> findBySubjectId(Long subjectId);
    List<Task> findByUserIdAndTaskDateOrderByStartTimeAsc(Long userId, LocalDate taskDate);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.taskDate < :date AND t.isCompleted = false")
    List<Task> findMissedTasks(Long userId, LocalDate date);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.taskDate = :date")
    Integer countTasksForDate(Long userId, LocalDate date);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.taskDate = :date AND t.isCompleted = true")
    Integer countCompletedTasksForDate(Long userId, LocalDate date);
}
