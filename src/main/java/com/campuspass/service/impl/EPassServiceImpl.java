package com.campuspass.service.impl;

import com.campuspass.dto.response.EPassResponse;
import com.campuspass.entity.Application;
import com.campuspass.entity.EPass;
import com.campuspass.entity.enums.PassStatus;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.EPassRepository;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.EPassService;
import com.campuspass.service.NotificationService;
import com.campuspass.util.QrCodeGeneratorUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class EPassServiceImpl implements EPassService {

    private final EPassRepository ePassRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final int gracePeriodMinutes;

    public EPassServiceImpl(
            EPassRepository ePassRepository,
            NotificationService notificationService,
            AuditLogService auditLogService,
            @Value("${campuspass.defaults.grace-period-minutes:15}") int gracePeriodMinutes) {
        this.ePassRepository = ePassRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    @Override
    @Transactional
    public EPass generateEPass(Application application) {
        // Build pass number PASS-2026-XXXXX
        String passNumber = "PASS-" + Year.now().getValue() + "-" + String.format("%05d", ThreadLocalRandom.current().nextInt(10000, 99999));
        
        // Cryptographic high-entropy random token
        String qrToken = "urn:campuspass:token:" + UUID.randomUUID().toString();

        LocalDateTime validFrom = LocalDateTime.of(application.getLeaveDate(), application.getLeaveTime());
        // Valid until is expected return time + grace period buffer
        LocalDateTime validUntil = application.getExpectedReturnDateTime().plusMinutes(gracePeriodMinutes + 60);

        EPass pass = new EPass(
                passNumber,
                application,
                application.getStudent(),
                qrToken,
                validFrom,
                validUntil,
                PassStatus.ACTIVE
        );
        pass = ePassRepository.save(pass);

        // Notify Student
        notificationService.createNotification(
                application.getStudent().getUser().getId(),
                "🎉 Digital E-Pass Generated",
                "Your application " + application.getApplicationNumber() + " is approved! E-Pass " + passNumber + " is active.",
                "EPASS_GENERATED",
                pass.getId()
        );

        auditLogService.log(
                application.getStudent().getUser().getId(),
                application.getStudent().getUser().getUsername(),
                "SYSTEM",
                "GENERATE_EPASS",
                "EPass",
                pass.getId(),
                "Digital E-Pass issued with number: " + passNumber
        );

        return pass;
    }

    @Override
    @Transactional(readOnly = true)
    public EPassResponse getActivePassForStudent(Long studentId) {
        EPass pass = ePassRepository.findFirstByStudentIdAndStatusOrderByIssuedAtDesc(studentId, PassStatus.ACTIVE)
                .orElse(null);
        if (pass == null) {
            return null;
        }
        return mapToResponse(pass);
    }

    @Override
    @Transactional(readOnly = true)
    public EPassResponse getPassById(Long passId) {
        EPass pass = ePassRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("EPass", "id", passId));
        return mapToResponse(pass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EPassResponse> getPassHistoryForStudent(Long studentId) {
        return ePassRepository.findByStudentIdOrderByIssuedAtDesc(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EPass findByQrToken(String qrToken) {
        return ePassRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ResourceNotFoundException("Pass not found for given QR token"));
    }

    @Override
    public byte[] getQrCodeImage(Long passId) {
        EPass pass = ePassRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("EPass", "id", passId));
        return QrCodeGeneratorUtil.generateQrCodeImageBytes(pass.getQrToken(), 300, 300);
    }

    private EPassResponse mapToResponse(EPass p) {
        String base64Qr = QrCodeGeneratorUtil.generateQrCodeBase64(p.getQrToken(), 250, 250);

        return new EPassResponse(
                p.getId(),
                p.getPassNumber(),
                p.getApplication().getApplicationNumber(),
                p.getStudent().getId(),
                p.getStudent().getUser().getUsername(),
                p.getStudent().getRollNumber(),
                p.getStudent().getDepartment().getName(),
                p.getStudent().getStudentPhotoUrl(),
                p.getValidFrom(),
                p.getValidUntil(),
                p.getStatus(),
                p.getQrToken(),
                base64Qr,
                p.getIssuedAt()
        );
    }
}
