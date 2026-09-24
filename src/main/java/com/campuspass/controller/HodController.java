package com.campuspass.controller;

import com.campuspass.dto.request.HodDecisionRequest;
import com.campuspass.dto.response.*;
import com.campuspass.entity.enums.ApplicationStatus;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.HodService;
import com.campuspass.service.PassExtensionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hod")
public class HodController {

    private final HodService hodService;
    private final PassExtensionService passExtensionService;

    public HodController(HodService hodService, PassExtensionService passExtensionService) {
        this.hodService = hodService;
        this.passExtensionService = passExtensionService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<HodDashboardMetrics>> getDashboardMetrics(@AuthenticationPrincipal UserPrincipal principal) {
        HodDashboardMetrics metrics = hodService.getDashboardMetrics(principal.getDepartmentId());
        return ResponseEntity.ok(ApiResponse.success("HOD dashboard metrics fetched", metrics));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApplications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) ApplicationStatus status) {
        List<ApplicationResponse> list = hodService.getDepartmentApplications(principal.getDepartmentId(), status);
        return ResponseEntity.ok(ApiResponse.success("Applications fetched", list));
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<ApiResponse<ApplicationResponse>> approveApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) HodDecisionRequest request) {
        String remarks = request != null ? request.getRemarks() : "Approved by HOD";
        ApplicationResponse response = hodService.approveApplication(id, principal.getHodId(), remarks);
        return ResponseEntity.ok(ApiResponse.success("Application approved and E-Pass issued", response));
    }

    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<ApiResponse<ApplicationResponse>> rejectApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody HodDecisionRequest request) {
        String reason = request != null ? request.getRemarks() : null;
        ApplicationResponse response = hodService.rejectApplication(id, principal.getHodId(), reason);
        return ResponseEntity.ok(ApiResponse.success("Application rejected", response));
    }

    @PostMapping("/applications/{id}/clarify")
    public ResponseEntity<ApiResponse<ApplicationResponse>> requestClarification(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody HodDecisionRequest request) {
        String notes = request != null ? request.getRemarks() : null;
        ApplicationResponse response = hodService.requestClarification(id, principal.getHodId(), notes);
        return ResponseEntity.ok(ApiResponse.success("Clarification requested from student", response));
    }

    @GetMapping("/live-movement")
    public ResponseEntity<ApiResponse<List<MovementResponse>>> getDepartmentLiveMovement(@AuthenticationPrincipal UserPrincipal principal) {
        List<MovementResponse> list = hodService.getDepartmentLiveMovement(principal.getDepartmentId());
        return ResponseEntity.ok(ApiResponse.success("Department movement records fetched", list));
    }

    @GetMapping("/extensions")
    public ResponseEntity<ApiResponse<List<PassExtensionResponse>>> getPendingExtensions(@AuthenticationPrincipal UserPrincipal principal) {
        List<PassExtensionResponse> list = passExtensionService.getPendingExtensions(principal.getDepartmentId());
        return ResponseEntity.ok(ApiResponse.success("Pending extensions fetched", list));
    }

    @PostMapping("/extensions/{id}/approve")
    public ResponseEntity<ApiResponse<PassExtensionResponse>> approveExtension(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) HodDecisionRequest request) {
        String remarks = request != null ? request.getRemarks() : "Approved by HOD";
        PassExtensionResponse response = passExtensionService.reviewExtension(id, principal.getHodId(), true, remarks);
        return ResponseEntity.ok(ApiResponse.success("Extension approved", response));
    }

    @PostMapping("/extensions/{id}/reject")
    public ResponseEntity<ApiResponse<PassExtensionResponse>> rejectExtension(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody(required = false) HodDecisionRequest request) {
        String remarks = request != null ? request.getRemarks() : "Rejected by HOD";
        PassExtensionResponse response = passExtensionService.reviewExtension(id, principal.getHodId(), false, remarks);
        return ResponseEntity.ok(ApiResponse.success("Extension rejected", response));
    }
}
