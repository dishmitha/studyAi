package com.studysync.dto;

import com.studysync.model.Subject;
import java.time.LocalDate;

public class SubjectRequest {
    private String name;
    private String description;
    private LocalDate examDate;
    private Subject.Priority priority;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Subject.Priority getPriority() {
        return priority;
    }

    public void setPriority(Subject.Priority priority) {
        this.priority = priority;
    }
}
