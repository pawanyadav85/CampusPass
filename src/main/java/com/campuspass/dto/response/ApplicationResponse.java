package com.campuspass.dto.response;

import com.campuspass.entity.enums.ApplicationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationResponse {

    private Long id;
    private String applicationNumber;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private String studentPhotoUrl;
    private Long departmentId;
    private String departmentCode;
    private String departmentName;
    private String subject;
    private String reasonText;
    private boolean emergency;
    private LocalDate leaveDate;
    private LocalTime leaveTime;
    private LocalDateTime expectedReturnDateTime;
    private ApplicationStatus status;
    private String hodRemarks;
    private List<DocumentResponse> documents = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ApplicationResponse() {
    }

    public ApplicationResponse(Long id, String applicationNumber, Long studentId, String studentName,
                               String studentRollNumber, String studentPhotoUrl, Long departmentId,
                               String departmentCode, String departmentName, String subject,
                               String reasonText, boolean emergency, LocalDate leaveDate,
                               LocalTime leaveTime, LocalDateTime expectedReturnDateTime,
                               ApplicationStatus status, String hodRemarks, List<DocumentResponse> documents,
                               LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.applicationNumber = applicationNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNumber = studentRollNumber;
        this.studentPhotoUrl = studentPhotoUrl;
        this.departmentId = departmentId;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.subject = subject;
        this.reasonText = reasonText;
        this.emergency = emergency;
        this.leaveDate = leaveDate;
        this.leaveTime = leaveTime;
        this.expectedReturnDateTime = expectedReturnDateTime;
        this.status = status;
        this.hodRemarks = hodRemarks;
        this.documents = documents != null ? documents : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
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

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentPhotoUrl() {
        return studentPhotoUrl;
    }

    public void setStudentPhotoUrl(String studentPhotoUrl) {
        this.studentPhotoUrl = studentPhotoUrl;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getHodRemarks() {
        return hodRemarks;
    }

    public void setHodRemarks(String hodRemarks) {
        this.hodRemarks = hodRemarks;
    }

    public List<DocumentResponse> getDocuments() {
        return documents;
    }

    public void setDocuments(List<DocumentResponse> documents) {
        this.documents = documents;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
