package com.campuspass.controller;

import com.campuspass.dto.request.PassExtensionRequest;
import com.campuspass.dto.response.ApiResponse;
import com.campuspass.dto.response.EPassResponse;
import com.campuspass.dto.response.PassExtensionResponse;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.EPassService;
import com.campuspass.service.PassExtensionService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/epasses")
public class EPassController {

    private final EPassService ePassService;
    private final PassExtensionService passExtensionService;

    public EPassController(EPassService ePassService, PassExtensionService passExtensionService) {
        this.ePassService = ePassService;
        this.passExtensionService = passExtensionService;
    }

    @GetMapping("/my-active")
    public ResponseEntity<ApiResponse<EPassResponse>> getMyActivePass(@AuthenticationPrincipal UserPrincipal principal) {
        EPassResponse pass = ePassService.getActivePassForStudent(principal.getStudentId());
        return ResponseEntity.ok(ApiResponse.success("Active pass fetched", pass));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EPassResponse>> getPassById(@PathVariable Long id) {
        EPassResponse pass = ePassService.getPassById(id);
        return ResponseEntity.ok(ApiResponse.success("Pass details fetched", pass));
    }

    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<EPassResponse>>> getMyPassHistory(@AuthenticationPrincipal UserPrincipal principal) {
        List<EPassResponse> history = ePassService.getPassHistoryForStudent(principal.getStudentId());
        return ResponseEntity.ok(ApiResponse.success("Pass history fetched", history));
    }

    @GetMapping(value = "/{id}/qr-image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrImage(@PathVariable Long id) {
        byte[] imageBytes = ePassService.getQrCodeImage(id);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageBytes);
    }

    @PostMapping("/{id}/request-extension")
    public ResponseEntity<ApiResponse<PassExtensionResponse>> requestExtension(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PassExtensionRequest request) {
        PassExtensionResponse response = passExtensionService.requestExtension(principal.getStudentId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Pass extension requested successfully", response));
    }
}
