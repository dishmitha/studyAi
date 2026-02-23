package com.studysync.repository;

import com.studysync.model.Subject;
import com.studysync.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByUser(User user);
    List<Subject> findByUserId(Long userId);
    List<Subject> findByUserIdAndPriority(Long userId, Subject.Priority priority);
    List<Subject> findByUserIdAndExamDateBefore(Long userId, LocalDate examDate);
    List<Subject> findByUserIdOrderByPriorityDesc(Long userId);
}
