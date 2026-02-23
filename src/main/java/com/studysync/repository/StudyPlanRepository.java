package com.studysync.repository;

import com.studysync.model.StudyPlan;
import com.studysync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    List<StudyPlan> findByUser(User user);
    List<StudyPlan> findByUserId(Long userId);
    Optional<StudyPlan> findByUserIdAndIsActiveTrue(Long userId);
    List<StudyPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
}
