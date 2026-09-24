package com.campuspass.controller;

import com.campuspass.dto.request.StudentProfileUpdateRequest;
import com.campuspass.dto.response.ApiResponse;
import com.campuspass.dto.response.StudentProfileResponse;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        StudentProfileResponse profile = studentService.getProfileByUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody StudentProfileUpdateRequest request) {
        StudentProfileResponse updated = studentService.updateProfile(principal.getStudentId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadPhoto(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        String path = studentService.uploadStudentPhoto(principal.getStudentId(), file);
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", Map.of("photoUrl", path)));
    }

    @PostMapping(value = "/id-card", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadIdCard(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("file") MultipartFile file) {
        String path = studentService.uploadIdCard(principal.getStudentId(), file);
        return ResponseEntity.ok(ApiResponse.success("ID card uploaded successfully", Map.of("idCardUrl", path)));
    }
}
