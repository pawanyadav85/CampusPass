package com.campuspass.service;

import com.campuspass.dto.request.CreateStaffRequest;
import com.campuspass.dto.request.DepartmentCreateRequest;
import com.campuspass.dto.request.SystemSettingUpdateRequest;
import com.campuspass.dto.response.AdminDashboardMetrics;
import com.campuspass.dto.response.UserSummaryResponse;
import com.campuspass.entity.AuditLog;
import com.campuspass.entity.Department;
import com.campuspass.entity.SystemSetting;

import java.util.List;

public interface AdminService {
    AdminDashboardMetrics getDashboardMetrics();
    List<UserSummaryResponse> getAllUsers();
    void toggleUserStatus(Long userId, Long adminUserId);
    UserSummaryResponse createStaffUser(CreateStaffRequest request, Long adminUserId);
    List<Department> getAllDepartments();
    Department createDepartment(DepartmentCreateRequest request, Long adminUserId);
    List<SystemSetting> getAllSettings();
    void updateSetting(SystemSettingUpdateRequest request, Long adminUserId);
    List<AuditLog> getRecentAuditLogs();
}
