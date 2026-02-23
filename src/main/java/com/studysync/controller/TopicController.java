package com.studysync.controller;

import com.studysync.dto.TopicRequest;
import com.studysync.model.Subject;
import com.studysync.model.Topic;
import com.studysync.service.SubjectService;
import com.studysync.service.TopicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

    private final TopicService topicService;
    private final SubjectService subjectService;

    public TopicController(TopicService topicService, SubjectService subjectService) {
        this.topicService = topicService;
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody TopicRequest request) {
        if (request.getSubjectId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setEstimatedHours(request.getEstimatedHours() != null ? request.getEstimatedHours() : 1.0);

        Topic created = topicService.createTopic(topic, request.getSubjectId());
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Topic>> getTopicsBySubject(@RequestParam Long subjectId) {
        List<Topic> topics = topicService.getTopicsBySubjectId(subjectId);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Topic>> getTopicsByUser(@PathVariable Long userId) {
        List<Topic> topics = topicService.getTopicsByUserId(userId);
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long id) {
        return topicService.getTopicById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Topic> updateTopic(@PathVariable Long id, @RequestBody TopicRequest request) {
        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setEstimatedHours(request.getEstimatedHours());
        topic.setScheduledDate(request.getEstimatedHours() != null ? null : null);

        Topic updated = topicService.updateTopic(id, topic);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopic(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Topic> markTopicComplete(@PathVariable Long id) {
        Topic topic = new Topic();
        topic.setIsCompleted(true);
        
        Topic updated = topicService.updateTopic(id, topic);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
}
