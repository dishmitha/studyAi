package com.studysync.repository;

import com.studysync.model.Reminder;
import com.studysync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUser(User user);
    List<Reminder> findByUserId(Long userId);
    List<Reminder> findByUserIdAndIsActiveTrue(Long userId);
    
    @Query("SELECT r FROM Reminder r WHERE r.isActive = true AND r.isSent = false AND r.reminderTime <= :currentTime")
    List<Reminder> findPendingReminders(LocalDateTime currentTime);
    
    @Query("SELECT r FROM Reminder r WHERE r.user.id = :userId AND r.reminderTime BETWEEN :startTime AND :endTime")
    List<Reminder> findRemindersForTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);
}
