package com.campuspass.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentRegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotBlank(message = "Department code is required")
    private String departmentCode;

    @NotNull(message = "Semester is required")
    private Integer semester;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Parent/Guardian name is required")
    private String parentName;

    @NotBlank(message = "Parent/Guardian phone is required")
    private String parentPhone;

    public StudentRegisterRequest() {
    }

    public StudentRegisterRequest(String username, String password, String email, String rollNumber,
                                  String departmentCode, Integer semester, String mobile,
                                  String parentName, String parentPhone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.rollNumber = rollNumber;
        this.departmentCode = departmentCode;
        this.semester = semester;
        this.mobile = mobile;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
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
}
