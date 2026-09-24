package com.campuspass.service.impl;

import com.campuspass.dto.response.ApplicationResponse;
import com.campuspass.dto.response.DocumentResponse;
import com.campuspass.dto.response.HodDashboardMetrics;
import com.campuspass.dto.response.MovementResponse;
import com.campuspass.entity.Application;
import com.campuspass.entity.GateMovement;
import com.campuspass.entity.Hod;
import com.campuspass.entity.enums.ApplicationStatus;
import com.campuspass.entity.enums.MovementStatus;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.ApplicationRepository;
import com.campuspass.repository.GateMovementRepository;
import com.campuspass.repository.HodRepository;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.EPassService;
import com.campuspass.service.HodService;
import com.campuspass.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HodServiceImpl implements HodService {

    private final ApplicationRepository applicationRepository;
    private final GateMovementRepository gateMovementRepository;
    private final HodRepository hodRepository;
    private final EPassService ePassService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public HodServiceImpl(
            ApplicationRepository applicationRepository,
            GateMovementRepository gateMovementRepository,
            HodRepository hodRepository,
            EPassService ePassService,
            NotificationService notificationService,
            AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.gateMovementRepository = gateMovementRepository;
        this.hodRepository = hodRepository;
        this.ePassService = ePassService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public HodDashboardMetrics getDashboardMetrics(Long departmentId) {
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        long pending = applicationRepository.countByDepartmentIdAndStatus(departmentId, ApplicationStatus.SUBMITTED)
                     + applicationRepository.countByDepartmentIdAndStatus(departmentId, ApplicationStatus.UNDER_REVIEW)
                     + applicationRepository.countByDepartmentIdAndStatus(departmentId, ApplicationStatus.RESUBMITTED);

        long approvedToday = applicationRepository.countByDepartmentAndStatusToday(departmentId, ApplicationStatus.APPROVED, startOfDay, endOfDay);
        long rejectedToday = applicationRepository.countByDepartmentAndStatusToday(departmentId, ApplicationStatus.REJECTED, startOfDay, endOfDay);
        long clarification = applicationRepository.countByDepartmentIdAndStatus(departmentId, ApplicationStatus.CLARIFICATION_REQUIRED);

        long outside = gateMovementRepository.countByDepartmentAndStatus(departmentId, MovementStatus.OUTSIDE);
        long overdue = gateMovementRepository.countByDepartmentAndStatus(departmentId, MovementStatus.OVERDUE);

        return new HodDashboardMetrics(pending, approvedToday, rejectedToday, clarification, outside, overdue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getDepartmentApplications(Long departmentId, ApplicationStatus status) {
        List<Application> list;
        if (status != null) {
            list = applicationRepository.findByDepartmentIdAndStatusOrderByCreatedAtDesc(departmentId, status);
        } else {
            list = applicationRepository.findDepartmentApplicationsWithEmergencyPriority(departmentId, null);
        }
        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse approveApplication(Long applicationId, Long hodId, String remarks) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        Hod hod = hodRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("Hod", "id", hodId));

        if (!application.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Unauthorized: Application belongs to a different department");
        }

        if (application.getStatus() == ApplicationStatus.APPROVED) {
            throw new BadRequestException("Application is already approved");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setHodRemarks(remarks != null && !remarks.isBlank() ? remarks : "Approved by HOD");
        application = applicationRepository.save(application);

        // Generate Digital E-Pass
        ePassService.generateEPass(application);

        auditLogService.log(
                hod.getUser().getId(),
                hod.getUser().getUsername(),
                "HOD",
                "APPROVE_APPLICATION",
                "Application",
                application.getId(),
                "HOD approved application: " + application.getApplicationNumber()
        );

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse rejectApplication(Long applicationId, Long hodId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BadRequestException("Rejection reason is mandatory");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        Hod hod = hodRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("Hod", "id", hodId));

        if (!application.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Unauthorized: Application belongs to a different department");
        }

        application.setStatus(ApplicationStatus.REJECTED);
        application.setHodRemarks(reason.trim());
        application = applicationRepository.save(application);

        // Notify Student
        notificationService.createNotification(
                application.getStudent().getUser().getId(),
                "Application Rejected",
                "Your application " + application.getApplicationNumber() + " was rejected. Reason: " + reason,
                "APPLICATION_REJECTED",
                application.getId()
        );

        auditLogService.log(
                hod.getUser().getId(),
                hod.getUser().getUsername(),
                "HOD",
                "REJECT_APPLICATION",
                "Application",
                application.getId(),
                "HOD rejected application with reason: " + reason
        );

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse requestClarification(Long applicationId, Long hodId, String notes) {
        if (notes == null || notes.trim().isEmpty()) {
            throw new BadRequestException("Clarification notes are mandatory");
        }

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        Hod hod = hodRepository.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("Hod", "id", hodId));

        if (!application.getDepartment().getId().equals(hod.getDepartment().getId())) {
            throw new BadRequestException("Unauthorized: Application belongs to a different department");
        }

        application.setStatus(ApplicationStatus.CLARIFICATION_REQUIRED);
        application.setHodRemarks(notes.trim());
        application = applicationRepository.save(application);

        // Notify Student
        notificationService.createNotification(
                application.getStudent().getUser().getId(),
                "Clarification Requested",
                "HOD requested clarification for application " + application.getApplicationNumber() + ": " + notes,
                "CLARIFICATION_REQUESTED",
                application.getId()
        );

        auditLogService.log(
                hod.getUser().getId(),
                hod.getUser().getUsername(),
                "HOD",
                "REQUEST_CLARIFICATION",
                "Application",
                application.getId(),
                "HOD requested clarification: " + notes
        );

        return mapToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponse> getDepartmentLiveMovement(Long departmentId) {
        List<GateMovement> movements = gateMovementRepository.findByDepartmentAndMovementStatuses(
                departmentId, Arrays.asList(MovementStatus.OUTSIDE, MovementStatus.OVERDUE)
        );
        return movements.stream().map(this::mapToMovementResponse).collect(Collectors.toList());
    }

    private ApplicationResponse mapToResponse(Application app) {
        List<DocumentResponse> docs = new ArrayList<>();
        if (app.getDocuments() != null) {
            docs = app.getDocuments().stream()
                    .map(d -> new DocumentResponse(
                            d.getId(),
                            d.getFileName(),
                            d.getFileType(),
                            d.getFileSizeBytes(),
                            d.getUploadedAt()))
                    .collect(Collectors.toList());
        }

        return new ApplicationResponse(
                app.getId(),
                app.getApplicationNumber(),
                app.getStudent().getId(),
                app.getStudent().getUser().getUsername(),
                app.getStudent().getRollNumber(),
                app.getStudent().getStudentPhotoUrl(),
                app.getDepartment().getId(),
                app.getDepartment().getCode(),
                app.getDepartment().getName(),
                app.getSubject(),
                app.getReasonText(),
                app.isEmergency(),
                app.getLeaveDate(),
                app.getLeaveTime(),
                app.getExpectedReturnDateTime(),
                app.getStatus(),
                app.getHodRemarks(),
                docs,
                app.getCreatedAt(),
                app.getUpdatedAt()
        );
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
