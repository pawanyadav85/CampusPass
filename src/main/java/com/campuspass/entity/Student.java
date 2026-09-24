package com.campuspass.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "roll_number", nullable = false, unique = true, length = 50)
    private String rollNumber;

    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false, length = 15)
    private String mobile;

    @Column(name = "parent_name", nullable = false, length = 100)
    private String parentName;

    @Column(name = "parent_phone", nullable = false, length = 15)
    private String parentPhone;

    @Column(name = "student_photo_url")
    private String studentPhotoUrl;

    @Column(name = "id_card_doc_url")
    private String idCardDocUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Student() {
    }

    public Student(User user, Department department, String rollNumber, Integer semester, String mobile, String parentName, String parentPhone) {
        this.user = user;
        this.department = department;
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.mobile = mobile;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }

    public String getStudentPhotoUrl() {
        return studentPhotoUrl;
    }

    public void setStudentPhotoUrl(String studentPhotoUrl) {
        this.studentPhotoUrl = studentPhotoUrl;
    }

    public String getIdCardDocUrl() {
        return idCardDocUrl;
    }

    public void setIdCardDocUrl(String idCardDocUrl) {
        this.idCardDocUrl = idCardDocUrl;
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
