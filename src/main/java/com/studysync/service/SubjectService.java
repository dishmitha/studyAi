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
    private final HistoryService historyService;

    public SubjectService(SubjectRepository subjectRepository, UserRepository userRepository, HistoryService historyService) {
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.historyService = historyService;
    }

    public Subject createSubject(Subject subject, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            subject.setUser(user.get());
            Subject saved = subjectRepository.save(subject);
            
            // Add history for subject creation
            historyService.addSubjectHistory(user.get(), saved.getId(), saved.getName(), 
                HistoryService.ACTION_CREATED, 
                "Created new subject: " + saved.getName());
            
            return saved;
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
            User user = updatedSubject.getUser();
            
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
                // Track when topics are completed
                if (subject.getCompletedTopics() > updatedSubject.getCompletedTopics()) {
                    historyService.addSubjectHistory(user, updatedSubject.getId(), updatedSubject.getName(),
                        HistoryService.ACTION_COMPLETED,
                        "Completed " + (subject.getCompletedTopics() - updatedSubject.getCompletedTopics()) 
                        + " topics in " + updatedSubject.getName());
                }
                updatedSubject.setCompletedTopics(subject.getCompletedTopics());
            }
            if (subject.getExamDate() != null) {
                updatedSubject.setExamDate(subject.getExamDate());
            }
            if (subject.getPriority() != null) {
                updatedSubject.setPriority(subject.getPriority());
            }
            
            Subject saved = subjectRepository.save(updatedSubject);
            
            // Add history for subject update
            historyService.addSubjectHistory(user, saved.getId(), saved.getName(),
                HistoryService.ACTION_UPDATED,
                "Updated subject: " + saved.getName());
            
            return saved;
        }
        return null;
    }

    public void deleteSubject(Long id) {
        Optional<Subject> subject = subjectRepository.findById(id);
        if (subject.isPresent()) {
            User user = subject.get().getUser();
            String subjectName = subject.get().getName();
            
            subjectRepository.deleteById(id);
            
            // Add history for subject deletion
            historyService.addSubjectHistory(user, id, subjectName,
                HistoryService.ACTION_DELETED,
                "Deleted subject: " + subjectName);
        }
    }
}
