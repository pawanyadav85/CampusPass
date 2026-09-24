package com.campuspass.service;

import com.campuspass.dto.request.ApplicationCreateRequest;
import com.campuspass.dto.request.ClarificationResponseRequest;
import com.campuspass.dto.response.ApplicationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse createApplication(Long studentId, ApplicationCreateRequest request, List<MultipartFile> files);
    List<ApplicationResponse> getStudentApplications(Long studentId);
    ApplicationResponse getApplicationDetails(Long applicationId);
    ApplicationResponse respondToClarification(Long applicationId, Long studentId, ClarificationResponseRequest request, List<MultipartFile> files);
    void cancelApplication(Long applicationId, Long studentId);
}
