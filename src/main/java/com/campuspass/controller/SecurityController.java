package com.campuspass.controller;

import com.campuspass.dto.request.GateScanRequest;
import com.campuspass.dto.response.ApiResponse;
import com.campuspass.dto.response.MovementResponse;
import com.campuspass.dto.response.VerificationResponse;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyQrToken(@Valid @RequestBody GateScanRequest request) {
        VerificationResponse response = securityService.verifyQrToken(request.getQrToken());
        return ResponseEntity.ok(ApiResponse.success("Verification evaluated", response));
    }

    @PostMapping("/record-exit")
    public ResponseEntity<ApiResponse<MovementResponse>> recordExit(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody GateScanRequest request) {
        MovementResponse response = securityService.recordExit(principal.getSecurityUserId(), request.getQrToken(), request.getRemarks());
        return ResponseEntity.ok(ApiResponse.success("Student exit recorded successfully", response));
    }

    @PostMapping("/record-return")
    public ResponseEntity<ApiResponse<MovementResponse>> recordReturn(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody GateScanRequest request) {
        MovementResponse response = securityService.recordReturn(principal.getSecurityUserId(), request.getQrToken(), request.getRemarks());
        return ResponseEntity.ok(ApiResponse.success("Student return recorded successfully", response));
    }

    @GetMapping("/currently-outside")
    public ResponseEntity<ApiResponse<List<MovementResponse>>> getCurrentlyOutside() {
        List<MovementResponse> list = securityService.getCurrentlyOutside();
        return ResponseEntity.ok(ApiResponse.success("Currently outside students fetched", list));
    }

    @GetMapping("/overdue-students")
    public ResponseEntity<ApiResponse<List<MovementResponse>>> getOverdueStudents() {
        List<MovementResponse> list = securityService.getOverdueStudents();
        return ResponseEntity.ok(ApiResponse.success("Overdue students fetched", list));
    }
}
