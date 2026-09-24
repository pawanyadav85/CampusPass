package com.campuspass.service.impl;

import com.campuspass.dto.request.CreateStaffRequest;
import com.campuspass.dto.request.DepartmentCreateRequest;
import com.campuspass.dto.request.SystemSettingUpdateRequest;
import com.campuspass.dto.response.AdminDashboardMetrics;
import com.campuspass.dto.response.UserSummaryResponse;
import com.campuspass.entity.*;
import com.campuspass.entity.enums.MovementStatus;
import com.campuspass.entity.enums.PassStatus;
import com.campuspass.entity.enums.Role;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.*;
import com.campuspass.service.AdminService;
import com.campuspass.service.AuditLogService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final HodRepository hodRepository;
    private final SecurityUserRepository securityUserRepository;
    private final DepartmentRepository departmentRepository;
    private final ApplicationRepository applicationRepository;
    private final EPassRepository ePassRepository;
    private final GateMovementRepository gateMovementRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(
            UserRepository userRepository,
            StudentRepository studentRepository,
            HodRepository hodRepository,
            SecurityUserRepository securityUserRepository,
            DepartmentRepository departmentRepository,
            ApplicationRepository applicationRepository,
            EPassRepository ePassRepository,
            GateMovementRepository gateMovementRepository,
            SystemSettingRepository systemSettingRepository,
            AuditLogService auditLogService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.hodRepository = hodRepository;
        this.securityUserRepository = securityUserRepository;
        this.departmentRepository = departmentRepository;
        this.applicationRepository = applicationRepository;
        this.ePassRepository = ePassRepository;
        this.gateMovementRepository = gateMovementRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.auditLogService = auditLogService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDashboardMetrics getDashboardMetrics() {
        long students = studentRepository.count();
        long hods = hodRepository.count();
        long security = securityUserRepository.count();
        long apps = applicationRepository.count();
        long activePasses = ePassRepository.count();
        long outside = gateMovementRepository.countByMovementStatus(MovementStatus.OUTSIDE);
        long overdue = gateMovementRepository.countByMovementStatus(MovementStatus.OVERDUE);

        return new AdminDashboardMetrics(students, hods, security, apps, activePasses, outside, overdue);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserSummaryResponse(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole(),
                        u.isActive(),
                        u.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long userId, Long adminUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setActive(!user.isActive());
        userRepository.save(user);

        auditLogService.log(
                adminUserId,
                "ADMIN",
                "ADMIN",
                "TOGGLE_USER_STATUS",
                "User",
                user.getId(),
                "User status changed to active=" + user.isActive() + " for: " + user.getUsername()
        );
    }

    @Override
    @Transactional
    public UserSummaryResponse createStaffUser(CreateStaffRequest request, Long adminUserId) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                request.getRole(),
                true
        );
        user = userRepository.save(user);

        if (request.getRole() == Role.HOD) {
            Department dept = departmentRepository.findByCode(request.getDepartmentCode().toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "code", request.getDepartmentCode()));

            Hod hod = new Hod(user, dept, request.getEmployeeId(), request.getOfficeRoom(), request.getPhone());
            hodRepository.save(hod);
        } else if (request.getRole() == Role.SECURITY) {
            SecurityUser secUser = new SecurityUser(user, request.getBadgeId(), request.getGateNumber(), request.getPhone());
            securityUserRepository.save(secUser);
        }

        auditLogService.log(
                adminUserId,
                "ADMIN",
                "ADMIN",
                "CREATE_STAFF_USER",
                "User",
                user.getId(),
                "Created new staff user: " + user.getUsername() + " (" + request.getRole() + ")"
        );

        return new UserSummaryResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isActive(), user.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional
    public Department createDepartment(DepartmentCreateRequest request, Long adminUserId) {
        if (departmentRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new BadRequestException("Department code already exists: " + request.getCode());
        }

        Department department = new Department(
                null,
                request.getCode().toUpperCase(),
                request.getName(),
                request.getDescription(),
                true
        );
        department = departmentRepository.save(department);

        auditLogService.log(
                adminUserId,
                "ADMIN",
                "ADMIN",
                "CREATE_DEPARTMENT",
                "Department",
                department.getId(),
                "Created department: " + department.getCode()
        );

        return department;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }

    @Override
    @Transactional
    public void updateSetting(SystemSettingUpdateRequest request, Long adminUserId) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(request.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("Setting", "key", request.getKey()));

        setting.setSettingValue(request.getValue());
        systemSettingRepository.save(setting);

        auditLogService.log(
                adminUserId,
                "ADMIN",
                "ADMIN",
                "UPDATE_SYSTEM_SETTING",
                "SystemSetting",
                setting.getId(),
                "Updated setting " + setting.getSettingKey() + " to: " + setting.getSettingValue()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogService.getRecentLogs();
    }
}
