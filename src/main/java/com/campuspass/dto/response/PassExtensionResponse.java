package com.campuspass.dto.response;

import com.campuspass.entity.enums.ExtensionStatus;

import java.time.LocalDateTime;

public class PassExtensionResponse {

    private Long id;
    private Long passId;
    private String passNumber;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private LocalDateTime requestedReturnTime;
    private String reason;
    private ExtensionStatus status;
    private String hodRemarks;
    private LocalDateTime createdAt;

    public PassExtensionResponse() {
    }

    public PassExtensionResponse(Long id, Long passId, String passNumber, Long studentId,
                                 String studentName, String rollNumber, LocalDateTime requestedReturnTime,
                                 String reason, ExtensionStatus status, String hodRemarks, LocalDateTime createdAt) {
        this.id = id;
        this.passId = passId;
        this.passNumber = passNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.requestedReturnTime = requestedReturnTime;
        this.reason = reason;
        this.status = status;
        this.hodRemarks = hodRemarks;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPassId() {
        return passId;
    }

    public void setPassId(Long passId) {
        this.passId = passId;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public LocalDateTime getRequestedReturnTime() {
        return requestedReturnTime;
    }

    public void setRequestedReturnTime(LocalDateTime requestedReturnTime) {
        this.requestedReturnTime = requestedReturnTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ExtensionStatus getStatus() {
        return status;
    }

    public void setStatus(ExtensionStatus status) {
        this.status = status;
    }

    public String getHodRemarks() {
        return hodRemarks;
    }

    public void setHodRemarks(String hodRemarks) {
        this.hodRemarks = hodRemarks;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
