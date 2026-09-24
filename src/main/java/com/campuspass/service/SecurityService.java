package com.campuspass.service;

import com.campuspass.dto.response.MovementResponse;
import com.campuspass.dto.response.VerificationResponse;

import java.util.List;

public interface SecurityService {
    VerificationResponse verifyQrToken(String qrToken);
    MovementResponse recordExit(Long securityUserId, String qrToken, String remarks);
    MovementResponse recordReturn(Long securityUserId, String qrToken, String remarks);
    List<MovementResponse> getCurrentlyOutside();
    List<MovementResponse> getOverdueStudents();
}
