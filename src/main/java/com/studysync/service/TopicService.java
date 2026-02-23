package com.studysync.service;

import com.studysync.model.Topic;
import com.studysync.model.Subject;
import com.studysync.repository.TopicRepository;
import com.studysync.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubjectRepository subjectRepository;

    public TopicService(TopicRepository topicRepository, SubjectRepository subjectRepository) {
        this.topicRepository = topicRepository;
        this.subjectRepository = subjectRepository;
    }

    public Topic createTopic(Topic topic, Long subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        if (subject.isPresent()) {
            topic.setSubject(subject.get());
            Topic savedTopic = topicRepository.save(topic);
            
            // Update subject total topics count
            Subject subj = subject.get();
            subj.setTotalTopics(subj.getTotalTopics() + 1);
            subjectRepository.save(subj);
            
            return savedTopic;
        }
        return null;
    }

    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }

    public List<Topic> getTopicsBySubjectId(Long subjectId) {
        return topicRepository.findBySubjectId(subjectId);
    }

    public List<Topic> getIncompleteTopicsBySubjectId(Long subjectId) {
        return topicRepository.findBySubjectIdAndIsCompletedFalse(subjectId);
    }

    public List<Topic> getTopicsByUserId(Long userId) {
        return topicRepository.findBySubjectUserId(userId);
    }

    public List<Topic> getTopicsByScheduledDate(LocalDate date) {
        return topicRepository.findByScheduledDate(date);
    }

    public List<Topic> getIncompleteTopicsOrderedByDate() {
        return topicRepository.findByIsCompletedFalseOrderByScheduledDateAsc();
    }

    public Topic updateTopic(Long id, Topic topic) {
        Optional<Topic> existingTopic = topicRepository.findById(id);
        if (existingTopic.isPresent()) {
            Topic updatedTopic = existingTopic.get();
            
            // Handle completion status change
            if (topic.getIsCompleted() != null && !updatedTopic.getIsCompleted() && topic.getIsCompleted()) {
                updatedTopic.setIsCompleted(true);
                updatedTopic.setCompletedDate(LocalDate.now());
                updatedTopic.setCompletedHours(topic.getEstimatedHours());
                
                // Update subject completed topics count
                Subject subject = updatedTopic.getSubject();
                subject.setCompletedTopics(subject.getCompletedTopics() + 1);
                subjectRepository.save(subject);
            }
            
            if (topic.getName() != null) {
                updatedTopic.setName(topic.getName());
            }
            if (topic.getDescription() != null) {
                updatedTopic.setDescription(topic.getDescription());
            }
            if (topic.getEstimatedHours() != null) {
                updatedTopic.setEstimatedHours(topic.getEstimatedHours());
            }
            if (topic.getScheduledDate() != null) {
                updatedTopic.setScheduledDate(topic.getScheduledDate());
            }
            
            return topicRepository.save(updatedTopic);
        }
        return null;
    }

    public void deleteTopic(Long id) {
        Optional<Topic> topic = topicRepository.findById(id);
        if (topic.isPresent()) {
            // Update subject total topics count
            Subject subject = topic.get().getSubject();
            if (subject != null) {
                subject.setTotalTopics(Math.max(0, subject.getTotalTopics() - 1));
                if (topic.get().getIsCompleted()) {
                    subject.setCompletedTopics(Math.max(0, subject.getCompletedTopics() - 1));
                }
                subjectRepository.save(subject);
            }
        }
        topicRepository.deleteById(id);
    }
}
