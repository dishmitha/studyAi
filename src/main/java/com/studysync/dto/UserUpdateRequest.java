package com.studysync.dto;

import java.time.LocalDate;

public class UserUpdateRequest {
    private String name;
    private String phone;
    private LocalDate dateOfBirth;
    private String institution;
    private Integer dailyStudyHours;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public Integer getDailyStudyHours() {
        return dailyStudyHours;
    }

    public void setDailyStudyHours(Integer dailyStudyHours) {
        this.dailyStudyHours = dailyStudyHours;
    }
}
