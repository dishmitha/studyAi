package com.studysync.repository;

import com.studysync.model.Progress;
import com.studysync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUser(User user);
    List<Progress> findByUserId(Long userId);
    Optional<Progress> findByUserIdAndProgressDate(Long userId, LocalDate progressDate);
    List<Progress> findByUserIdOrderByProgressDateDesc(Long userId);
    List<Progress> findByUserIdAndProgressDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT COUNT(p) FROM Progress p WHERE p.user.id = :userId AND p.completionPercentage > 0")
    Integer countActiveDays(Long userId);
    
    @Query("SELECT SUM(p.studyHours) FROM Progress p WHERE p.user.id = :userId")
    Double totalStudyHours(Long userId);
    
    @Query(value = "SELECT * FROM progress p WHERE p.user_id = :userId AND p.completion_percentage = 100 ORDER BY p.progress_date DESC LIMIT :limit", nativeQuery = true)
    List<Progress> findPerfectDays(Long userId, Integer limit);
}
