package com.campuspass.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ApplicationCreateRequest {

    @NotBlank(message = "Application subject is required")
    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    @NotBlank(message = "Detailed explanation/reason text is required in your own words")
    private String reasonText;

    private boolean emergency = false;

    @NotNull(message = "Leaving date is required")
    @FutureOrPresent(message = "Leaving date cannot be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate leaveDate;

    @NotNull(message = "Leaving time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime leaveTime;

    @NotNull(message = "Expected return date and time is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime expectedReturnDateTime;

    public ApplicationCreateRequest() {
    }

    public ApplicationCreateRequest(String subject, String reasonText, boolean emergency,
                                  LocalDate leaveDate, LocalTime leaveTime, LocalDateTime expectedReturnDateTime) {
        this.subject = subject;
        this.reasonText = reasonText;
        this.emergency = emergency;
        this.leaveDate = leaveDate;
        this.leaveTime = leaveTime;
        this.expectedReturnDateTime = expectedReturnDateTime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(LocalDate leaveDate) {
        this.leaveDate = leaveDate;
    }

    public LocalTime getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(LocalTime leaveTime) {
        this.leaveTime = leaveTime;
    }

    public LocalDateTime getExpectedReturnDateTime() {
        return expectedReturnDateTime;
    }

    public void setExpectedReturnDateTime(LocalDateTime expectedReturnDateTime) {
        this.expectedReturnDateTime = expectedReturnDateTime;
    }
}
