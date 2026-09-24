package com.campuspass.service.impl;

import com.campuspass.dto.request.ChangePasswordRequest;
import com.campuspass.dto.request.LoginRequest;
import com.campuspass.dto.request.StudentRegisterRequest;
import com.campuspass.dto.response.AuthResponse;
import com.campuspass.entity.Department;
import com.campuspass.entity.Student;
import com.campuspass.entity.User;
import com.campuspass.entity.enums.Role;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.DepartmentRepository;
import com.campuspass.repository.StudentRepository;
import com.campuspass.repository.UserRepository;
import com.campuspass.security.JwtTokenProvider;
import com.campuspass.security.UserPrincipal;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserRepository userRepository,
            StudentRepository studentRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        auditLogService.log(
                principal.getId(),
                principal.getUsername(),
                principal.getRole().name(),
                "USER_LOGIN",
                "User",
                principal.getId(),
                "User successfully logged in via credentials"
        );

        String deptName = null;
        if (principal.getDepartmentId() != null) {
            deptName = departmentRepository.findById(principal.getDepartmentId())
                    .map(Department::getName)
                    .orElse(null);
        }

        return new AuthResponse(
                jwt,
                "Bearer",
                principal.getId(),
                principal.getUsername(),
                principal.getEmail(),
                principal.getRole(),
                principal.getDepartmentId(),
                deptName,
                principal.getStudentId(),
                principal.getHodId(),
                principal.getSecurityUserId(),
                principal.getUsername()
        );
    }

    @Override
    @Transactional
    public AuthResponse registerStudent(StudentRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        if (studentRepository.existsByRollNumber(request.getRollNumber())) {
            throw new BadRequestException("Roll number is already registered");
        }

        Department department = departmentRepository.findByCode(request.getDepartmentCode().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "code", request.getDepartmentCode()));

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                Role.STUDENT,
                true
        );
        user = userRepository.save(user);

        Student student = new Student(
                user,
                department,
                request.getRollNumber(),
                request.getSemester(),
                request.getMobile(),
                request.getParentName(),
                request.getParentPhone()
        );
        student = studentRepository.save(student);

        auditLogService.log(
                user.getId(),
                user.getUsername(),
                Role.STUDENT.name(),
                "STUDENT_REGISTER",
                "Student",
                student.getId(),
                "New student registered: " + student.getRollNumber()
        );

        LoginRequest loginRequest = new LoginRequest(request.getUsername(), request.getPassword());
        return login(loginRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse getCurrentUser(UserPrincipal principal) {
        String deptName = null;
        if (principal.getDepartmentId() != null) {
            deptName = departmentRepository.findById(principal.getDepartmentId())
                    .map(Department::getName)
                    .orElse(null);
        }

        return new AuthResponse(
                null,
                "Bearer",
                principal.getId(),
                principal.getUsername(),
                principal.getEmail(),
                principal.getRole(),
                principal.getDepartmentId(),
                deptName,
                principal.getStudentId(),
                principal.getHodId(),
                principal.getSecurityUserId(),
                principal.getUsername()
        );
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                "CHANGE_PASSWORD",
                "User",
                user.getId(),
                "User changed their password"
        );
    }
}
