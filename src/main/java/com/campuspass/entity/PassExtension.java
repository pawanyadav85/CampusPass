package com.campuspass.entity;

import com.campuspass.entity.enums.ExtensionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pass_extensions")
public class PassExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_id", nullable = false)
    private EPass pass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "requested_return_time", nullable = false)
    private LocalDateTime requestedReturnTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hod_id")
    private Hod hod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExtensionStatus status = ExtensionStatus.PENDING;

    @Column(name = "hod_remarks", columnDefinition = "TEXT")
    private String hodRemarks;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public PassExtension() {
    }

    public PassExtension(EPass pass, Student student, LocalDateTime requestedReturnTime, String reason) {
        this.pass = pass;
        this.student = student;
        this.requestedReturnTime = requestedReturnTime;
        this.reason = reason;
        this.status = ExtensionStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EPass getPass() {
        return pass;
    }

    public void setPass(EPass pass) {
        this.pass = pass;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    public Hod getHod() {
        return hod;
    }

    public void setHod(Hod hod) {
        this.hod = hod;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
