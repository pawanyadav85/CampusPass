package com.campuspass.service.impl;

import com.campuspass.dto.request.StudentProfileUpdateRequest;
import com.campuspass.dto.response.StudentProfileResponse;
import com.campuspass.entity.Student;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.StudentRepository;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.FileStorageService;
import com.campuspass.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    public StudentServiceImpl(StudentRepository studentRepository,
                              FileStorageService fileStorageService,
                              AuditLogService auditLogService) {
        this.studentRepository = studentRepository;
        this.fileStorageService = fileStorageService;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        return mapToResponse(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfileByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for user id: " + userId));
        return mapToResponse(student);
    }

    @Override
    @Transactional
    public StudentProfileResponse updateProfile(Long studentId, StudentProfileUpdateRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        student.setMobile(request.getMobile());
        student.setParentName(request.getParentName());
        student.setParentPhone(request.getParentPhone());
        student = studentRepository.save(student);

        auditLogService.log(
                student.getUser().getId(),
                student.getUser().getUsername(),
                "STUDENT",
                "UPDATE_PROFILE",
                "Student",
                student.getId(),
                "Student updated personal contact information"
        );

        return mapToResponse(student);
    }

    @Override
    @Transactional
    public String uploadStudentPhoto(Long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (student.getStudentPhotoUrl() != null) {
            fileStorageService.deleteFile(student.getStudentPhotoUrl());
        }

        String path = fileStorageService.storeFile(file, "photos");
        student.setStudentPhotoUrl(path);
        studentRepository.save(student);

        auditLogService.log(
                student.getUser().getId(),
                student.getUser().getUsername(),
                "STUDENT",
                "UPLOAD_PHOTO",
                "Student",
                student.getId(),
                "Student uploaded profile photo: " + path
        );

        return path;
    }

    @Override
    @Transactional
    public String uploadIdCard(Long studentId, MultipartFile file) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (student.getIdCardDocUrl() != null) {
            fileStorageService.deleteFile(student.getIdCardDocUrl());
        }

        String path = fileStorageService.storeFile(file, "id_cards");
        student.setIdCardDocUrl(path);
        studentRepository.save(student);

        auditLogService.log(
                student.getUser().getId(),
                student.getUser().getUsername(),
                "STUDENT",
                "UPLOAD_ID_CARD",
                "Student",
                student.getId(),
                "Student uploaded ID card document: " + path
        );

        return path;
    }

    private StudentProfileResponse mapToResponse(Student s) {
        return new StudentProfileResponse(
                s.getId(),
                s.getUser().getId(),
                s.getUser().getUsername(),
                s.getUser().getEmail(),
                s.getRollNumber(),
                s.getDepartment().getId(),
                s.getDepartment().getCode(),
                s.getDepartment().getName(),
                s.getSemester(),
                s.getMobile(),
                s.getParentName(),
                s.getParentPhone(),
                s.getStudentPhotoUrl(),
                s.getIdCardDocUrl()
        );
    }
}
