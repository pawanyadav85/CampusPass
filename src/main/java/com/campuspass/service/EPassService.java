package com.campuspass.service;

import com.campuspass.dto.response.EPassResponse;
import com.campuspass.entity.Application;
import com.campuspass.entity.EPass;

import java.util.List;

public interface EPassService {
    EPass generateEPass(Application application);
    EPassResponse getActivePassForStudent(Long studentId);
    EPassResponse getPassById(Long passId);
    List<EPassResponse> getPassHistoryForStudent(Long studentId);
    EPass findByQrToken(String qrToken);
    byte[] getQrCodeImage(Long passId);
}
