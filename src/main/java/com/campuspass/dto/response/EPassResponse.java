package com.campuspass.dto.response;

import com.campuspass.entity.enums.PassStatus;

import java.time.LocalDateTime;

public class EPassResponse {

    private Long id;
    private String passNumber;
    private String applicationNumber;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String departmentName;
    private String studentPhotoUrl;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private PassStatus status;
    private String qrToken;
    private String qrCodeBase64;
    private LocalDateTime issuedAt;

    public EPassResponse() {
    }

    public EPassResponse(Long id, String passNumber, String applicationNumber, Long studentId,
                         String studentName, String rollNumber, String departmentName,
                         String studentPhotoUrl, LocalDateTime validFrom, LocalDateTime validUntil,
                         PassStatus status, String qrToken, String qrCodeBase64, LocalDateTime issuedAt) {
        this.id = id;
        this.passNumber = passNumber;
        this.applicationNumber = applicationNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.departmentName = departmentName;
        this.studentPhotoUrl = studentPhotoUrl;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.status = status;
        this.qrToken = qrToken;
        this.qrCodeBase64 = qrCodeBase64;
        this.issuedAt = issuedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassNumber() {
        return passNumber;
    }

    public void setPassNumber(String passNumber) {
        this.passNumber = passNumber;
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

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getStudentPhotoUrl() {
        return studentPhotoUrl;
    }

    public void setStudentPhotoUrl(String studentPhotoUrl) {
        this.studentPhotoUrl = studentPhotoUrl;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public PassStatus getStatus() {
        return status;
    }

    public void setStatus(PassStatus status) {
        this.status = status;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }
}
