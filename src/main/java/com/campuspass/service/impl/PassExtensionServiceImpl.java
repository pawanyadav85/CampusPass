package com.campuspass.service.impl;

import com.campuspass.dto.request.PassExtensionRequest;
import com.campuspass.dto.response.PassExtensionResponse;
import com.campuspass.entity.EPass;
import com.campuspass.entity.GateMovement;
import com.campuspass.entity.Hod;
import com.campuspass.entity.PassExtension;
import com.campuspass.entity.enums.ExtensionStatus;
import com.campuspass.entity.enums.MovementStatus;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.EPassRepository;
import com.campuspass.repository.GateMovementRepository;
import com.campuspass.repository.HodRepository;
import com.campuspass.repository.PassExtensionRepository;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.NotificationService;
import com.campuspass.service.PassExtensionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PassExtensionServiceImpl implements PassExtensionService {

    private final PassExtensionRepository extensionRepository;
    private final EPassRepository ePassRepository;
    private final GateMovementRepository gateMovementRepository;
    private final HodRepository hodRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public PassExtensionServiceImpl(
            PassExtensionRepository extensionRepository,
            EPassRepository ePassRepository,
            GateMovementRepository gateMovementRepository,
            HodRepository hodRepository,
            NotificationService notificationService,
            AuditLogService auditLogService) {
        this.extensionRepository = extensionRepository;
        this.ePassRepository = ePassRepository;
        this.gateMovementRepository = gateMovementRepository;
        this.hodRepository = hodRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public PassExtensionResponse requestExtension(Long studentId, Long passId, PassExtensionRequest request) {
        EPass pass = ePassRepository.findById(passId)
                .orElseThrow(() -> new ResourceNotFoundException("EPass not found"));

        if (!pass.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("Unauthorized: Pass belongs to another student");
        }

        // Verify open movement
        Optional<GateMovement> movementOpt = gateMovementRepository.findFirstByPassIdOrderByCreatedAtDesc(passId);
        if (movementOpt.isEmpty() || 
            (movementOpt.get().getMovementStatus() != MovementStatus.OUTSIDE && 
             movementOpt.get().getMovementStatus() != MovementStatus.OVERDUE)) {
            throw new BadRequestException("Cannot request extension: Student is not recorded as outside campus");
        }

        GateMovement movement = movementOpt.get();
        if (request.getRequestedReturnTime().isBefore(movement.getExpectedReturnTime())) {
            throw new BadRequestException("Requested return time must be later than current expected return time: " + movement.getExpectedReturnTime());
        }

        PassExtension extension = new PassExtension(
                pass,
                pass.getStudent(),
                request.getRequestedReturnTime(),
                request.getReason()
        );
        extension = extensionRepository.save(extension);

        // Notify HOD
        Hod hod = hodRepository.findByDepartmentId(pass.getStudent().getDepartment().getId()).orElse(null);
        if (hod != null) {
            notificationService.createNotification(
                    hod.getUser().getId(),
                    "Pass Extension Request: " + pass.getStudent().getRollNumber(),
                    "Student requested extension until " + request.getRequestedReturnTime() + ". Reason: " + request.getReason(),
                    "EXTENSION_REQUESTED",
                    extension.getId()
            );
        }

        auditLogService.log(
                pass.getStudent().getUser().getId(),
                pass.getStudent().getUser().getUsername(),
                "STUDENT",
                "REQUEST_EXTENSION",
                "PassExtension",
                extension.getId(),
                "Extension requested until " + request.getRequestedReturnTime()
        );

        return mapToResponse(extension);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PassExtensionResponse> getPendingExtensions(Long departmentId) {
        return extensionRepository.findByDepartmentAndStatus(departmentId, ExtensionStatus.PENDING)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PassExtensionResponse reviewExtension(Long extensionId, Long hodId, boolean approved, String remarks) {
        PassExtension extension = extensionRepository.findById(extensionId)
                .orElseThrow(() -> new ResourceNotFoundException("PassExtension not found"));

        Hod hod = hodRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("Hod not found"));

        if (!extension.getStudent().getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Unauthorized: Request belongs to a different department");
        }

        if (extension.getStatus() != ExtensionStatus.PENDING) {
            throw new BadRequestException("Extension is already reviewed");
        }

        extension.setHod(hod);
        extension.setHodRemarks(remarks);

        if (approved) {
            extension.setStatus(ExtensionStatus.APPROVED);

            // Update gate movement expected return time
            GateMovement movement = gateMovementRepository.findFirstByPassIdOrderByCreatedAtDesc(extension.getPass().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Movement not found for pass"));
            
            movement.setExpectedReturnTime(extension.getRequestedReturnTime());
            // If movement was overdue, reset to OUTSIDE since expected return time is pushed forward
            if (movement.getMovementStatus() == MovementStatus.OVERDUE) {
                movement.setMovementStatus(MovementStatus.OUTSIDE);
            }
            gateMovementRepository.save(movement);

            // Also update pass validUntil
            extension.getPass().setValidUntil(extension.getRequestedReturnTime().plusMinutes(60));
            ePassRepository.save(extension.getPass());

            // Notify Student
            notificationService.createNotification(
                    extension.getStudent().getUser().getId(),
                    "✅ Pass Extension Approved",
                    "Your pass extension was approved by HOD. New expected return time: " + extension.getRequestedReturnTime(),
                    "EXTENSION_APPROVED",
                    extension.getId()
            );

            auditLogService.log(
                    hod.getUser().getId(),
                    hod.getUser().getUsername(),
                    "HOD",
                    "APPROVE_EXTENSION",
                    "PassExtension",
                    extension.getId(),
                    "HOD approved extension until " + extension.getRequestedReturnTime()
            );
        } else {
            extension.setStatus(ExtensionStatus.REJECTED);

            // Notify Student
            notificationService.createNotification(
                    extension.getStudent().getUser().getId(),
                    "❌ Pass Extension Rejected",
                    "Your pass extension was rejected by HOD. Remarks: " + remarks,
                    "EXTENSION_REJECTED",
                    extension.getId()
            );

            auditLogService.log(
                    hod.getUser().getId(),
                    hod.getUser().getUsername(),
                    "HOD",
                    "REJECT_EXTENSION",
                    "PassExtension",
                    extension.getId(),
                    "HOD rejected extension. Remarks: " + remarks
            );
        }

        extension = extensionRepository.save(extension);
        return mapToResponse(extension);
    }

    private PassExtensionResponse mapToResponse(PassExtension pe) {
        return new PassExtensionResponse(
                pe.getId(),
                pe.getPass().getId(),
                pe.getPass().getPassNumber(),
                pe.getStudent().getId(),
                pe.getStudent().getUser().getUsername(),
                pe.getStudent().getRollNumber(),
                pe.getRequestedReturnTime(),
                pe.getReason(),
                pe.getStatus(),
                pe.getHodRemarks(),
                pe.getCreatedAt()
        );
    }
}
