package com.campuspass.service;

import com.campuspass.dto.request.PassExtensionRequest;
import com.campuspass.dto.response.PassExtensionResponse;

import java.util.List;

public interface PassExtensionService {
    PassExtensionResponse requestExtension(Long studentId, Long passId, PassExtensionRequest request);
    List<PassExtensionResponse> getPendingExtensions(Long departmentId);
    PassExtensionResponse reviewExtension(Long extensionId, Long hodId, boolean approved, String remarks);
}
