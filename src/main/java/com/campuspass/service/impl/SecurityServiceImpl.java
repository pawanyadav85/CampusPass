package com.campuspass.service.impl;

import com.campuspass.dto.response.MovementResponse;
import com.campuspass.dto.response.VerificationResponse;
import com.campuspass.entity.EPass;
import com.campuspass.entity.GateMovement;
import com.campuspass.entity.SecurityUser;
import com.campuspass.entity.enums.MovementStatus;
import com.campuspass.entity.enums.PassStatus;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.EPassRepository;
import com.campuspass.repository.GateMovementRepository;
import com.campuspass.repository.SecurityUserRepository;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.NotificationService;
import com.campuspass.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final EPassRepository ePassRepository;
    private final GateMovementRepository gateMovementRepository;
    private final SecurityUserRepository securityUserRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final int gracePeriodMinutes;

    public SecurityServiceImpl(
            EPassRepository ePassRepository,
            GateMovementRepository gateMovementRepository,
            SecurityUserRepository securityUserRepository,
            NotificationService notificationService,
            AuditLogService auditLogService,
            @Value("${campuspass.defaults.grace-period-minutes:15}") int gracePeriodMinutes) {
        this.ePassRepository = ePassRepository;
        this.gateMovementRepository = gateMovementRepository;
        this.securityUserRepository = securityUserRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationResponse verifyQrToken(String qrToken) {
        if (qrToken == null || qrToken.trim().isEmpty()) {
            return VerificationResponse.invalid("INVALID_TOKEN", "QR token cannot be empty");
        }

        Optional<EPass> passOpt = ePassRepository.findByQrToken(qrToken.trim());
        if (passOpt.isEmpty()) {
            return VerificationResponse.invalid("INVALID_TOKEN", "Invalid QR token: Pass not found in system");
        }

        EPass pass = passOpt.get();
        LocalDateTime now = LocalDateTime.now();

        if (pass.getStatus() == PassStatus.REVOKED) {
            return VerificationResponse.invalid("REVOKED", "Pass has been revoked by campus administration");
        }
        if (pass.getStatus() == PassStatus.COMPLETED || pass.getStatus() == PassStatus.USED) {
            return VerificationResponse.invalid("ALREADY_RETURNED", "Pass already completed: Student has returned to campus");
        }
        if (pass.getStatus() == PassStatus.EXPIRED) {
            return VerificationResponse.invalid("EXPIRED", "Pass has expired without utilization");
        }

        // Check if movement currently open
        Optional<GateMovement> openMovementOpt = gateMovementRepository.findFirstByPassIdOrderByCreatedAtDesc(pass.getId());

        if (openMovementOpt.isPresent() && 
            (openMovementOpt.get().getMovementStatus() == MovementStatus.OUTSIDE || 
             openMovementOpt.get().getMovementStatus() == MovementStatus.OVERDUE)) {
            GateMovement gm = openMovementOpt.get();
            return new VerificationResponse(
                    true,
                    "VALID_FOR_RETURN",
                    "Valid Pass: Student is currently outside. Ready to record RETURN.",
                    pass.getId(),
                    pass.getPassNumber(),
                    pass.getStudent().getId(),
                    pass.getStudent().getUser().getUsername(),
                    pass.getStudent().getRollNumber(),
                    pass.getStudent().getDepartment().getName(),
                    pass.getStudent().getStudentPhotoUrl(),
                    pass.getValidFrom(),
                    pass.getValidUntil(),
                    gm.getExpectedReturnTime(),
                    gm.getMovementStatus().name()
            );
        }

        // Pass has not exited yet: verify departure time
        if (now.isBefore(pass.getValidFrom().minusMinutes(30))) {
            return VerificationResponse.invalid("OUTSIDE_VALID_TIME", "Too early: Departure window opens at " + pass.getValidFrom().toLocalTime());
        }

        if (now.isAfter(pass.getValidUntil())) {
            return VerificationResponse.invalid("EXPIRED", "Pass expired: Valid window ended at " + pass.getValidUntil());
        }

        return new VerificationResponse(
                true,
                "VALID_FOR_EXIT",
                "Valid Pass: Approved by HOD. Ready to record student EXIT.",
                pass.getId(),
                pass.getPassNumber(),
                pass.getStudent().getId(),
                pass.getStudent().getUser().getUsername(),
                pass.getStudent().getRollNumber(),
                pass.getStudent().getDepartment().getName(),
                pass.getStudent().getStudentPhotoUrl(),
                pass.getValidFrom(),
                pass.getValidUntil(),
                pass.getApplication().getExpectedReturnDateTime(),
                "NOT_EXITED"
        );
    }

    @Override
    @Transactional
    public MovementResponse recordExit(Long securityUserId, String qrToken, String remarks) {
        VerificationResponse verification = verifyQrToken(qrToken);
        if (!verification.isValid() || !"VALID_FOR_EXIT".equals(verification.getVerificationCode())) {
            throw new BadRequestException("Cannot record exit: " + verification.getMessage());
        }

        EPass pass = ePassRepository.findById(verification.getPassId())
                .orElseThrow(() -> new ResourceNotFoundException("EPass not found"));

        SecurityUser securityUser = securityUserRepository.findById(securityUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Security user not found"));

        LocalDateTime now = LocalDateTime.now();

        GateMovement movement = new GateMovement(
                pass,
                pass.getStudent(),
                securityUser,
                now,
                pass.getApplication().getExpectedReturnDateTime(),
                MovementStatus.OUTSIDE
        );
        movement.setGateRemarks(remarks);
        movement = gateMovementRepository.save(movement);

        // Notify Student
        notificationService.createNotification(
                pass.getStudent().getUser().getId(),
                "Gate Exit Recorded",
                "Exit recorded at Gate " + securityUser.getGateNumber() + " at " + now.toLocalTime().withNano(0) +
                        ". Expected return by " + pass.getApplication().getExpectedReturnDateTime(),
                "GATE_EXIT",
                movement.getId()
        );

        auditLogService.log(
                securityUser.getUser().getId(),
                securityUser.getUser().getUsername(),
                "SECURITY",
                "RECORD_EXIT",
                "GateMovement",
                movement.getId(),
                "Exit recorded for " + pass.getStudent().getRollNumber() + " through Gate " + securityUser.getGateNumber()
        );

        return mapToMovementResponse(movement);
    }

    @Override
    @Transactional
    public MovementResponse recordReturn(Long securityUserId, String qrToken, String remarks) {
        VerificationResponse verification = verifyQrToken(qrToken);
        if (!verification.isValid() || !"VALID_FOR_RETURN".equals(verification.getVerificationCode())) {
            throw new BadRequestException("Cannot record return: " + verification.getMessage());
        }

        EPass pass = ePassRepository.findById(verification.getPassId())
                .orElseThrow(() -> new ResourceNotFoundException("EPass not found"));

        SecurityUser securityUser = securityUserRepository.findById(securityUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Security user not found"));

        GateMovement movement = gateMovementRepository.findFirstByPassIdOrderByCreatedAtDesc(pass.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No open gate movement found for pass"));

        LocalDateTime now = LocalDateTime.now();
        movement.setReturnSecurity(securityUser);
        movement.setActualReturnTime(now);

        // Determine if returned on time or overdue
        LocalDateTime cutoffWithGrace = movement.getExpectedReturnTime().plusMinutes(gracePeriodMinutes);
        if (now.isAfter(cutoffWithGrace)) {
            movement.setMovementStatus(MovementStatus.RETURNED_OVERDUE);
        } else {
            movement.setMovementStatus(MovementStatus.RETURNED_ON_TIME);
        }

        if (remarks != null && !remarks.isBlank()) {
            movement.setGateRemarks((movement.getGateRemarks() != null ? movement.getGateRemarks() + " | Return: " : "Return: ") + remarks);
        }

        movement = gateMovementRepository.save(movement);

        // Finalize EPass to COMPLETED
        pass.setStatus(PassStatus.COMPLETED);
        ePassRepository.save(pass);

        // Notify Student
        notificationService.createNotification(
                pass.getStudent().getUser().getId(),
                "Gate Return Recorded",
                "Return recorded at Gate " + securityUser.getGateNumber() + " at " + now.toLocalTime().withNano(0) +
                        ". Pass finalized as " + movement.getMovementStatus(),
                "GATE_RETURN",
                movement.getId()
        );

        auditLogService.log(
                securityUser.getUser().getId(),
                securityUser.getUser().getUsername(),
                "SECURITY",
                "RECORD_RETURN",
                "GateMovement",
                movement.getId(),
                "Return recorded for " + pass.getStudent().getRollNumber() + " Status: " + movement.getMovementStatus()
        );

        return mapToMovementResponse(movement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> getCurrentlyOutside() {
        return gateMovementRepository.findByMovementStatusOrderByExitTimeDesc(MovementStatus.OUTSIDE)
                .stream()
                .map(this::mapToMovementResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> getOverdueStudents() {
        return gateMovementRepository.findByMovementStatusOrderByExitTimeDesc(MovementStatus.OVERDUE)
                .stream()
                .map(this::mapToMovementResponse)
                .collect(Collectors.toList());
    }

    private MovementResponse mapToMovementResponse(GateMovement gm) {
        return new MovementResponse(
                gm.getId(),
                gm.getPass().getId(),
                gm.getPass().getPassNumber(),
                gm.getStudent().getId(),
                gm.getStudent().getUser().getUsername(),
                gm.getStudent().getRollNumber(),
                gm.getStudent().getStudentPhotoUrl(),
                gm.getStudent().getDepartment().getName(),
                gm.getExitTime(),
                gm.getExpectedReturnTime(),
                gm.getActualReturnTime(),
                gm.getMovementStatus(),
                gm.getGateRemarks()
        );
    }
}
