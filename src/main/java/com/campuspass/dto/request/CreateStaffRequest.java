package com.campuspass.dto.request;

import com.campuspass.entity.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateStaffRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;

    @NotNull(message = "Role is required (HOD or SECURITY)")
    private Role role;

    private String departmentCode; // For HOD
    private String employeeId;    // For HOD
    private String officeRoom;    // For HOD
    private String badgeId;       // For Security
    private String gateNumber;    // For Security

    @NotBlank(message = "Phone number is required")
    private String phone;

    public CreateStaffRequest() {
    }

    public CreateStaffRequest(String username, String password, String email, Role role,
                              String departmentCode, String employeeId, String officeRoom,
                              String badgeId, String gateNumber, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.departmentCode = departmentCode;
        this.employeeId = employeeId;
        this.officeRoom = officeRoom;
        this.badgeId = badgeId;
        this.gateNumber = gateNumber;
        this.phone = phone;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getOfficeRoom() {
        return officeRoom;
    }

    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    public void setGateNumber(String gateNumber) {
        this.gateNumber = gateNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
