package com.campuspass.controller;

import com.campuspass.dto.request.ApplicationCreateRequest;
import com.campuspass.dto.request.ClarificationResponseRequest;
import com.campuspass.dto.response.ApiResponse;
import com.campuspass.dto.response.ApplicationResponse;
import com.campuspass.entity.ApplicationDocument;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.ApplicationDocumentRepository;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.ApplicationService;
import com.campuspass.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final FileStorageService fileStorageService;
    private final ApplicationDocumentRepository documentRepository;

    public ApplicationController(
            ApplicationService applicationService,
            FileStorageService fileStorageService,
            ApplicationDocumentRepository documentRepository) {
        this.applicationService = applicationService;
        this.fileStorageService = fileStorageService;
        this.documentRepository = documentRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @ModelAttribute ApplicationCreateRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        ApplicationResponse response = applicationService.createApplication(principal.getStudentId(), request, files);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ApplicationResponse> list = applicationService.getStudentApplications(principal.getStudentId());
        return ResponseEntity.ok(ApiResponse.success("Applications fetched", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplicationDetails(@PathVariable Long id) {
        ApplicationResponse response = applicationService.getApplicationDetails(id);
        return ResponseEntity.ok(ApiResponse.success("Application details fetched", response));
    }

    @PostMapping(value = "/{id}/clarification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ApplicationResponse>> respondToClarification(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @ModelAttribute ClarificationResponseRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files) {
        ApplicationResponse response = applicationService.respondToClarification(id, principal.getStudentId(), request, files);
        return ResponseEntity.ok(ApiResponse.success("Clarification submitted successfully", response));
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        applicationService.cancelApplication(id, principal.getStudentId());
        return ResponseEntity.ok(ApiResponse.success("Application cancelled successfully"));
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        ApplicationDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document", "id", documentId));

        Resource resource = fileStorageService.loadFileAsResource(doc.getFilePath());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.getFileType() != null ? doc.getFileType() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }
}
