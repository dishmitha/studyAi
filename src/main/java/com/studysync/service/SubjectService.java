package com.studysync.service;

import com.studysync.model.Subject;
import com.studysync.model.User;
import com.studysync.repository.SubjectRepository;
import com.studysync.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public SubjectService(SubjectRepository subjectRepository, UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    public Subject createSubject(Subject subject, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            subject.setUser(user.get());
            return subjectRepository.save(subject);
        }
        return null;
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> getSubjectsByUserId(Long userId) {
        return subjectRepository.findByUserId(userId);
    }

    public List<Subject> getSubjectsByPriority(Long userId, Subject.Priority priority) {
        return subjectRepository.findByUserIdAndPriority(userId, priority);
    }

    public List<Subject> getSubjectsByExamDateBefore(Long userId, java.time.LocalDate date) {
        return subjectRepository.findByUserIdAndExamDateBefore(userId, date);
    }

    public List<Subject> getSubjectsOrderedByPriority(Long userId) {
        return subjectRepository.findByUserIdOrderByPriorityDesc(userId);
    }

    public Subject updateSubject(Long id, Subject subject) {
        Optional<Subject> existingSubject = subjectRepository.findById(id);
        if (existingSubject.isPresent()) {
            Subject updatedSubject = existingSubject.get();
            if (subject.getName() != null) {
                updatedSubject.setName(subject.getName());
            }
            if (subject.getDescription() != null) {
                updatedSubject.setDescription(subject.getDescription());
            }
            if (subject.getTotalTopics() != null) {
                updatedSubject.setTotalTopics(subject.getTotalTopics());
            }
            if (subject.getCompletedTopics() != null) {
                updatedSubject.setCompletedTopics(subject.getCompletedTopics());
            }
            if (subject.getExamDate() != null) {
                updatedSubject.setExamDate(subject.getExamDate());
            }
            if (subject.getPriority() != null) {
                updatedSubject.setPriority(subject.getPriority());
            }
            return subjectRepository.save(updatedSubject);
        }
        return null;
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}
