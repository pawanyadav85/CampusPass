package com.campuspass.service;

import com.campuspass.dto.response.ApplicationResponse;
import com.campuspass.dto.response.HodDashboardMetrics;
import com.campuspass.dto.response.MovementResponse;
import com.campuspass.entity.enums.ApplicationStatus;

import java.util.List;

public interface HodService {
    HodDashboardMetrics getDashboardMetrics(Long departmentId);
    List<ApplicationResponse> getDepartmentApplications(Long departmentId, ApplicationStatus status);
    ApplicationResponse approveApplication(Long applicationId, Long hodId, String remarks);
    ApplicationResponse rejectApplication(Long applicationId, Long hodId, String reason);
    ApplicationResponse requestClarification(Long applicationId, Long hodId, String notes);
    List<MovementResponse> getDepartmentLiveMovement(Long departmentId);
}
