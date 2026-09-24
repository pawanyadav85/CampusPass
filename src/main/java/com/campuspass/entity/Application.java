package com.campuspass.entity;

import com.campuspass.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_number", nullable = false, unique = true, length = 30)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(name = "reason_text", nullable = false, columnDefinition = "TEXT")
    private String reasonText;

    @Column(name = "is_emergency", nullable = false)
    private boolean emergency = false;

    @Column(name = "leave_date", nullable = false)
    private LocalDate leaveDate;

    @Column(name = "leave_time", nullable = false)
    private LocalTime leaveTime;

    @Column(name = "expected_return_date_time", nullable = false)
    private LocalDateTime expectedReturnDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(name = "hod_remarks", columnDefinition = "TEXT")
    private String hodRemarks;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationDocument> documents = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Application() {
    }

    public Application(String applicationNumber, Student student, Department department, String subject,
                       String reasonText, boolean emergency, LocalDate leaveDate, LocalTime leaveTime,
                       LocalDateTime expectedReturnDateTime, ApplicationStatus status) {
        this.applicationNumber = applicationNumber;
        this.student = student;
        this.department = department;
        this.subject = subject;
        this.reasonText = reasonText;
        this.emergency = emergency;
        this.leaveDate = leaveDate;
        this.leaveTime = leaveTime;
        this.expectedReturnDateTime = expectedReturnDateTime;
        this.status = status;
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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

    public List<ApplicationDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<ApplicationDocument> documents) {
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
