package com.campuspass.service;

import com.campuspass.dto.request.StudentProfileUpdateRequest;
import com.campuspass.dto.response.StudentProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    StudentProfileResponse getProfile(Long studentId);
    StudentProfileResponse getProfileByUserId(Long userId);
    StudentProfileResponse updateProfile(Long studentId, StudentProfileUpdateRequest request);
    String uploadStudentPhoto(Long studentId, MultipartFile file);
    String uploadIdCard(Long studentId, MultipartFile file);
}
