package com.campuspass.dto.response;

import com.campuspass.entity.enums.MovementStatus;

import java.time.LocalDateTime;

public class MovementResponse {

    private Long id;
    private Long passId;
    private String passNumber;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String studentPhotoUrl;
    private String departmentName;
    private LocalDateTime exitTime;
    private LocalDateTime expectedReturnTime;
    private LocalDateTime actualReturnTime;
    private MovementStatus movementStatus;
    private String gateRemarks;

    public MovementResponse() {
    }

    public MovementResponse(Long id, Long passId, String passNumber, Long studentId,
                            String studentName, String rollNumber, String studentPhotoUrl,
                            String departmentName, LocalDateTime exitTime, LocalDateTime expectedReturnTime,
                            LocalDateTime actualReturnTime, MovementStatus movementStatus, String gateRemarks) {
        this.id = id;
        this.passId = passId;
        this.passNumber = passNumber;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.studentPhotoUrl = studentPhotoUrl;
        this.departmentName = departmentName;
        this.exitTime = exitTime;
        this.expectedReturnTime = expectedReturnTime;
        this.actualReturnTime = actualReturnTime;
        this.movementStatus = movementStatus;
        this.gateRemarks = gateRemarks;
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

    public String getStudentPhotoUrl() {
        return studentPhotoUrl;
    }

    public void setStudentPhotoUrl(String studentPhotoUrl) {
        this.studentPhotoUrl = studentPhotoUrl;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(LocalDateTime expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public LocalDateTime getActualReturnTime() {
        return actualReturnTime;
    }

    public void setActualReturnTime(LocalDateTime actualReturnTime) {
        this.actualReturnTime = actualReturnTime;
    }

    public MovementStatus getMovementStatus() {
        return movementStatus;
    }

    public void setMovementStatus(MovementStatus movementStatus) {
        this.movementStatus = movementStatus;
    }

    public String getGateRemarks() {
        return gateRemarks;
    }

    public void setGateRemarks(String gateRemarks) {
        this.gateRemarks = gateRemarks;
    }
}
