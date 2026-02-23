package com.studysync.repository;

import com.studysync.model.Topic;
import com.studysync.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findBySubject(Subject subject);
    List<Topic> findBySubjectId(Long subjectId);
    List<Topic> findBySubjectIdAndIsCompletedFalse(Long subjectId);
    List<Topic> findBySubjectUserId(Long userId);
    List<Topic> findByScheduledDate(LocalDate scheduledDate);
    List<Topic> findBySubjectIdAndScheduledDateBefore(Long subjectId, LocalDate date);
    List<Topic> findByIsCompletedFalseOrderByScheduledDateAsc();
}
