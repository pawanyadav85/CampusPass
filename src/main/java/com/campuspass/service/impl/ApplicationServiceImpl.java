package com.campuspass.service.impl;

import com.campuspass.dto.request.ApplicationCreateRequest;
import com.campuspass.dto.request.ClarificationResponseRequest;
import com.campuspass.dto.response.ApplicationResponse;
import com.campuspass.dto.response.DocumentResponse;
import com.campuspass.entity.Application;
import com.campuspass.entity.ApplicationDocument;
import com.campuspass.entity.Hod;
import com.campuspass.entity.Student;
import com.campuspass.entity.enums.ApplicationStatus;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.ApplicationDocumentRepository;
import com.campuspass.repository.ApplicationRepository;
import com.campuspass.repository.HodRepository;
import com.campuspass.repository.StudentRepository;
import com.campuspass.service.ApplicationService;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.FileStorageService;
import com.campuspass.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final StudentRepository studentRepository;
    private final HodRepository hodRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            ApplicationDocumentRepository documentRepository,
            StudentRepository studentRepository,
            HodRepository hodRepository,
            FileStorageService fileStorageService,
            NotificationService notificationService,
            AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
        this.studentRepository = studentRepository;
        this.hodRepository = hodRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public ApplicationResponse createApplication(Long studentId, ApplicationCreateRequest request, List<MultipartFile> files) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        LocalDateTime departureDateTime = LocalDateTime.of(request.getLeaveDate(), request.getLeaveTime());
        if (request.getExpectedReturnDateTime().isBefore(departureDateTime)) {
            throw new BadRequestException("Expected return date/time must be strictly after the departure date/time");
        }

        String appNumber = "CP-" + Year.now().getValue() + "-" + String.format("%05d", ThreadLocalRandom.current().nextInt(10000, 99999));

        Application application = new Application(
                appNumber,
                student,
                student.getDepartment(),
                request.getSubject(),
                request.getReasonText(),
                request.isEmergency(),
                request.getLeaveDate(),
                request.getLeaveTime(),
                request.getExpectedReturnDateTime(),
                ApplicationStatus.SUBMITTED
        );
        application = applicationRepository.save(application);

        // Store supporting documents if provided
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String relativePath = fileStorageService.storeFile(file, "documents");
                    ApplicationDocument doc = new ApplicationDocument(
                            application,
                            file.getOriginalFilename(),
                            relativePath,
                            file.getContentType(),
                            file.getSize()
                    );
                    documentRepository.save(doc);
                    application.getDocuments().add(doc);
                }
            }
        }

        // Notify Department HOD
        Hod hod = hodRepository.findByDepartmentId(student.getDepartment().getId()).orElse(null);
        if (hod != null) {
            String title = request.isEmergency() ? "🚨 [EMERGENCY] New Permission Application" : "New Permission Application";
            String msg = "Student " + student.getUser().getUsername() + " (" + student.getRollNumber() + ") submitted application: " + appNumber;
            notificationService.createNotification(hod.getUser().getId(), title, msg, "APPLICATION_SUBMITTED", application.getId());
        }

        auditLogService.log(
                student.getUser().getId(),
                student.getUser().getUsername(),
                "STUDENT",
                "CREATE_APPLICATION",
                "Application",
                application.getId(),
                "Application created: " + appNumber + (request.isEmergency() ? " [EMERGENCY]" : "")
        );

        return mapToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getStudentApplications(Long studentId) {
        return applicationRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationDetails(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));
        return mapToResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse respondToClarification(Long applicationId, Long studentId, ClarificationResponseRequest request, List<MultipartFile> files) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        if (!application.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("Unauthorized: Application belongs to another student");
        }

        if (application.getStatus() != ApplicationStatus.CLARIFICATION_REQUIRED) {
            throw new BadRequestException("Application is not awaiting clarification. Current status: " + application.getStatus());
        }

        // Append student's reply
        application.setReasonText(application.getReasonText() + "\n\n[Clarification Reply]: " + request.getResponseRemarks());
        application.setStatus(ApplicationStatus.RESUBMITTED);

        // Upload any newly attached documents
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String relativePath = fileStorageService.storeFile(file, "documents");
                    ApplicationDocument doc = new ApplicationDocument(
                            application,
                            file.getOriginalFilename(),
                            relativePath,
                            file.getContentType(),
                            file.getSize()
                    );
                    documentRepository.save(doc);
                    application.getDocuments().add(doc);
                }
            }
        }

        application = applicationRepository.save(application);

        // Alert HOD
        Hod hod = hodRepository.findByDepartmentId(application.getDepartment().getId()).orElse(null);
        if (hod != null) {
            notificationService.createNotification(
                    hod.getUser().getId(),
                    "Clarification Responded: " + application.getApplicationNumber(),
                    "Student " + application.getStudent().getRollNumber() + " responded to clarification request.",
                    "CLARIFICATION_RESUBMITTED",
                    application.getId()
            );
        }

        auditLogService.log(
                application.getStudent().getUser().getId(),
                application.getStudent().getUser().getUsername(),
                "STUDENT",
                "RESPOND_CLARIFICATION",
                "Application",
                application.getId(),
                "Student provided requested clarification and resubmitted application"
        );

        return mapToResponse(application);
    }

    @Override
    @Transactional
    public void cancelApplication(Long applicationId, Long studentId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", "id", applicationId));

        if (!application.getStudent().getId().equals(studentId)) {
            throw new BadRequestException("Unauthorized: Application belongs to another student");
        }

        if (application.getStatus() != ApplicationStatus.SUBMITTED &&
            application.getStatus() != ApplicationStatus.CLARIFICATION_REQUIRED &&
            application.getStatus() != ApplicationStatus.UNDER_REVIEW) {
            throw new BadRequestException("Cannot cancel application in status: " + application.getStatus());
        }

        application.setStatus(ApplicationStatus.CANCELLED);
        applicationRepository.save(application);

        auditLogService.log(
                application.getStudent().getUser().getId(),
                application.getStudent().getUser().getUsername(),
                "STUDENT",
                "CANCEL_APPLICATION",
                "Application",
                application.getId(),
                "Student cancelled application: " + application.getApplicationNumber()
        );
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
}
