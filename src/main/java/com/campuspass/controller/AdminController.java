package com.campuspass.controller;

import com.campuspass.dto.request.CreateStaffRequest;
import com.campuspass.dto.request.DepartmentCreateRequest;
import com.campuspass.dto.request.SystemSettingUpdateRequest;
import com.campuspass.dto.response.AdminDashboardMetrics;
import com.campuspass.dto.response.ApiResponse;
import com.campuspass.dto.response.UserSummaryResponse;
import com.campuspass.entity.AuditLog;
import com.campuspass.entity.Department;
import com.campuspass.entity.SystemSetting;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardMetrics>> getDashboardMetrics() {
        AdminDashboardMetrics metrics = adminService.getDashboardMetrics();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard metrics fetched", metrics));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getAllUsers() {
        List<UserSummaryResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users fetched", users));
    }

    @PostMapping("/users/staff")
    public ResponseEntity<ApiResponse<UserSummaryResponse>> createStaffUser(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateStaffRequest request) {
        UserSummaryResponse created = adminService.createStaffUser(request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Staff user created successfully", created));
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        adminService.toggleUserStatus(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("User status updated"));
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        List<Department> departments = adminService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success("Departments fetched", departments));
    }

    @PostMapping("/departments")
    public ResponseEntity<ApiResponse<Department>> createDepartment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody DepartmentCreateRequest request) {
        Department department = adminService.createDepartment(request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Department created successfully", department));
    }

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<List<SystemSetting>>> getAllSettings() {
        List<SystemSetting> settings = adminService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.success("Settings fetched", settings));
    }

    @PutMapping("/settings")
    public ResponseEntity<ApiResponse<Void>> updateSetting(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody SystemSettingUpdateRequest request) {
        adminService.updateSetting(request, principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Setting updated successfully"));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        List<AuditLog> logs = adminService.getRecentAuditLogs();
        return ResponseEntity.ok(ApiResponse.success("Audit logs fetched", logs));
    }
}
