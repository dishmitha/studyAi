package com.studysync.service;

import com.studysync.model.History;
import com.studysync.model.User;
import com.studysync.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    // History Types
    public static final String TYPE_ACCOUNT = "ACCOUNT";
    public static final String TYPE_SUBJECT = "SUBJECT";
    public static final String TYPE_TASK = "TASK";
    public static final String TYPE_STUDY_SESSION = "STUDY_SESSION";

    // Actions
    public static final String ACTION_CREATED = "CREATED";
    public static final String ACTION_UPDATED = "UPDATED";
    public static final String ACTION_COMPLETED = "COMPLETED";
    public static final String ACTION_DELETED = "DELETED";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_REGISTERED = "REGISTERED";

    public void addAccountHistory(User user, String action, String description) {
        History history = new History();
        history.setType(TYPE_ACCOUNT);
        history.setAction(action);
        history.setDescription(description);
        history.setUser(user);
        history.setEntityId(user.getId());
        history.setEntityName(user.getName());
        historyRepository.save(history);
    }

    public void addSubjectHistory(User user, Long subjectId, String subjectName, String action, String description) {
        History history = new History();
        history.setType(TYPE_SUBJECT);
        history.setAction(action);
        history.setDescription(description);
        history.setUser(user);
        history.setEntityId(subjectId);
        history.setEntityName(subjectName);
        historyRepository.save(history);
    }

    public void addTaskHistory(User user, Long taskId, String taskTitle, String action, String description) {
        History history = new History();
        history.setType(TYPE_TASK);
        history.setAction(action);
        history.setDescription(description);
        history.setUser(user);
        history.setEntityId(taskId);
        history.setEntityName(taskTitle);
        historyRepository.save(history);
    }

    public void addStudySessionHistory(User user, String description, String metadata) {
        History history = new History();
        history.setType(TYPE_STUDY_SESSION);
        history.setAction(ACTION_COMPLETED);
        history.setDescription(description);
        history.setUser(user);
        history.setMetadata(metadata);
        historyRepository.save(history);
    }

    public List<History> getUserHistory(Long userId) {
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Page<History> getUserHistoryPaged(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<History> getUserHistoryByType(Long userId, String type) {
        return historyRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, type);
    }

    public List<History> getUserHistoryByAction(Long userId, String action) {
        return historyRepository.findByUserIdAndActionOrderByCreatedAtDesc(userId, action);
    }
    
    public History createHistory(History history) {
        return historyRepository.save(history);
    }
}
