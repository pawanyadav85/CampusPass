package com.campuspass.entity;

import com.campuspass.entity.enums.MovementStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gate_movements")
public class GateMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_id", nullable = false)
    private EPass pass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exit_security_id", nullable = false)
    private SecurityUser exitSecurity;

    @Column(name = "exit_time", nullable = false)
    private LocalDateTime exitTime;

    @Column(name = "expected_return_time", nullable = false)
    private LocalDateTime expectedReturnTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_security_id")
    private SecurityUser returnSecurity;

    @Column(name = "actual_return_time")
    private LocalDateTime actualReturnTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_status", nullable = false, length = 30)
    private MovementStatus movementStatus = MovementStatus.OUTSIDE;

    @Column(name = "gate_remarks", columnDefinition = "TEXT")
    private String gateRemarks;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public GateMovement() {
    }

    public GateMovement(EPass pass, Student student, SecurityUser exitSecurity, LocalDateTime exitTime,
                        LocalDateTime expectedReturnTime, MovementStatus movementStatus) {
        this.pass = pass;
        this.student = student;
        this.exitSecurity = exitSecurity;
        this.exitTime = exitTime;
        this.expectedReturnTime = expectedReturnTime;
        this.movementStatus = movementStatus;
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

    public SecurityUser getExitSecurity() {
        return exitSecurity;
    }

    public void setExitSecurity(SecurityUser exitSecurity) {
        this.exitSecurity = exitSecurity;
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

    public SecurityUser getReturnSecurity() {
        return returnSecurity;
    }

    public void setReturnSecurity(SecurityUser returnSecurity) {
        this.returnSecurity = returnSecurity;
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
