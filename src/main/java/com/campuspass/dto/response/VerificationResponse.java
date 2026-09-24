package com.campuspass.dto.response;

import java.time.LocalDateTime;

public class VerificationResponse {

    private boolean valid;
    private String verificationCode; // VALID_FOR_EXIT, VALID_FOR_RETURN, INVALID, EXPIRED, etc.
    private String message;
    private Long passId;
    private String passNumber;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String departmentName;
    private String studentPhotoUrl;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private LocalDateTime expectedReturnTime;
    private String currentMovementStatus;

    public VerificationResponse() {
    }

    public VerificationResponse(boolean valid, String verificationCode, String message, Long passId,
                                String passNumber, Long studentId, String studentName, String rollNumber,
                                String departmentName, String studentPhotoUrl, LocalDateTime validFrom,
                                LocalDateTime validUntil, LocalDateTime expectedReturnTime,
                                String currentMovementStatus) {
        this.valid = valid;
        this.verificationCode = verificationCode;
        this.message = message;
        this.passId = passId;
        this.passNumber = passNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.departmentName = departmentName;
        this.studentPhotoUrl = studentPhotoUrl;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.expectedReturnTime = expectedReturnTime;
        this.currentMovementStatus = currentMovementStatus;
    }

    public static VerificationResponse invalid(String code, String message) {
        return new VerificationResponse(false, code, message, null, null, null, null, null, null, null, null, null, null, null);
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(LocalDateTime expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public String getCurrentMovementStatus() {
        return currentMovementStatus;
    }

    public void setCurrentMovementStatus(String currentMovementStatus) {
        this.currentMovementStatus = currentMovementStatus;
    }
}
