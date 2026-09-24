package com.campuspass;

import com.campuspass.dto.request.ApplicationCreateRequest;
import com.campuspass.dto.request.LoginRequest;
import com.campuspass.dto.request.PassExtensionRequest;
import com.campuspass.dto.response.ApplicationResponse;
import com.campuspass.dto.response.AuthResponse;
import com.campuspass.dto.response.VerificationResponse;
import com.campuspass.entity.Department;
import com.campuspass.entity.EPass;
import com.campuspass.entity.Hod;
import com.campuspass.entity.Student;
import com.campuspass.entity.User;
import com.campuspass.entity.enums.Role;
import com.campuspass.exception.BadRequestException;
import com.campuspass.exception.ResourceNotFoundException;
import com.campuspass.repository.DepartmentRepository;
import com.campuspass.repository.EPassRepository;
import com.campuspass.repository.HodRepository;
import com.campuspass.repository.StudentRepository;
import com.campuspass.repository.UserRepository;
import com.campuspass.service.ApplicationService;
import com.campuspass.service.AuthService;
import com.campuspass.service.HodService;
import com.campuspass.service.PassExtensionService;
import com.campuspass.service.SecurityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EdgeCaseTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private HodService hodService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PassExtensionService passExtensionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private HodRepository hodRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EPassRepository ePassRepository;

    private Long studentId;
    private Long hodId;

    @BeforeEach
    void setUp() {
        AuthResponse studentAuth = authService.login(new LoginRequest("student1", "Password@123"));
        this.studentId = studentAuth.getStudentId();

        AuthResponse hodAuth = authService.login(new LoginRequest("hod_cse", "Password@123"));
        this.hodId = hodAuth.getHodId();
    }

    @Test
    void testInvalidReturnTimeBeforeDepartureThrowsBadRequest() {
        LocalDate leaveDate = LocalDate.now().plusDays(1);
        LocalTime leaveTime = LocalTime.of(14, 0);
        // Return time set to 2 hours BEFORE leave time on the same date
        LocalDateTime invalidReturn = LocalDateTime.of(leaveDate, LocalTime.of(12, 0));

        ApplicationCreateRequest request = new ApplicationCreateRequest(
                "Invalid Time Test",
                "Testing temporal invariants",
                false,
                leaveDate,
                leaveTime,
                invalidReturn
        );

        Assertions.assertThrows(BadRequestException.class, () -> {
            applicationService.createApplication(studentId, request, null);
        });
    }

    @Test
    void testRejectionWithoutReasonThrowsBadRequest() {
        LocalDate leaveDate = LocalDate.now();
        LocalTime leaveTime = LocalTime.now().withNano(0);
        LocalDateTime returnTime = LocalDateTime.now().plusHours(3);

        ApplicationCreateRequest request = new ApplicationCreateRequest(
                "Need leave",
                "Legitimate personal task",
                false,
                leaveDate,
                leaveTime,
                returnTime
        );

        ApplicationResponse app = applicationService.createApplication(studentId, request, null);

        // HOD attempts to reject with null or empty reason
        Assertions.assertThrows(BadRequestException.class, () -> {
            hodService.rejectApplication(app.getId(), hodId, null);
        });

        Assertions.assertThrows(BadRequestException.class, () -> {
            hodService.rejectApplication(app.getId(), hodId, "   ");
        });
    }

    @Test
    void testClarificationWithoutNotesThrowsBadRequest() {
        LocalDate leaveDate = LocalDate.now();
        LocalTime leaveTime = LocalTime.now().withNano(0);
        LocalDateTime returnTime = LocalDateTime.now().plusHours(3);

        ApplicationCreateRequest request = new ApplicationCreateRequest(
                "Hospital visit",
                "Consultation scheduled",
                true,
                leaveDate,
                leaveTime,
                returnTime
        );

        ApplicationResponse app = applicationService.createApplication(studentId, request, null);

        // HOD attempts clarification without notes
        Assertions.assertThrows(BadRequestException.class, () -> {
            hodService.requestClarification(app.getId(), hodId, "");
        });
    }

    @Test
    void testInvalidQrTokenVerificationFailsGracefully() {
        VerificationResponse res1 = securityService.verifyQrToken("invalid-nonexistent-token");
        Assertions.assertFalse(res1.isValid());
        Assertions.assertEquals("INVALID_TOKEN", res1.getVerificationCode());

        VerificationResponse res2 = securityService.verifyQrToken("");
        Assertions.assertFalse(res2.isValid());
        Assertions.assertEquals("INVALID_TOKEN", res2.getVerificationCode());
    }

    @Test
    void testExtensionRequestFailsWhenStudentIsNotOutside() {
        LocalDate leaveDate = LocalDate.now();
        LocalTime leaveTime = LocalTime.now().withNano(0);
        LocalDateTime returnTime = LocalDateTime.now().plusHours(3);

        ApplicationCreateRequest request = new ApplicationCreateRequest(
                "Outing",
                "Visit bookstore",
                false,
                leaveDate,
                leaveTime,
                returnTime
        );

        ApplicationResponse app = applicationService.createApplication(studentId, request, null);
        ApplicationResponse approved = hodService.approveApplication(app.getId(), hodId, "Approved");

        EPass pass = ePassRepository.findByApplicationId(approved.getId())
                .orElseThrow(() -> new IllegalStateException("Pass not found"));

        // Attempting to request extension before exiting gate
        PassExtensionRequest extRequest = new PassExtensionRequest(
                LocalDateTime.now().plusHours(5),
                "Delayed by traffic"
        );

        Assertions.assertThrows(BadRequestException.class, () -> {
            passExtensionService.requestExtension(studentId, pass.getId(), extRequest);
        });
    }

    @Test
    void testCrossDepartmentHodReviewThrowsBadRequest() {
        // Create an IT department and IT student
        Department itDept = departmentRepository.findByCode("IT").orElseGet(() ->
                departmentRepository.save(new Department(null, "IT", "Information Technology", "IT Dept", true))
        );

        User itUser = userRepository.save(new User("it_student_test", passwordEncoder.encode("Password@123"), "it.student@test.edu", Role.STUDENT, true));
        Student itStudent = studentRepository.save(new Student(itUser, itDept, "22IT999", 6, "9000000000", "Parent Name", "9000000001"));

        ApplicationCreateRequest request = new ApplicationCreateRequest(
                "IT Student Request",
                "Visiting software exhibition",
                false,
                LocalDate.now(),
                LocalTime.now().withNano(0),
                LocalDateTime.now().plusHours(4)
        );

        ApplicationResponse itApp = applicationService.createApplication(itStudent.getId(), request, null);

        // CSE HOD attempts to approve an IT student's application -> Unauthorized cross-department check
        Assertions.assertThrows(BadRequestException.class, () -> {
            hodService.approveApplication(itApp.getId(), hodId, "CSE HOD unauthorized approval");
        });
    }

    @Test
    void testStudentCannotCancelAnotherStudentsApplication() {
        Department cseDept = departmentRepository.findByCode("CSE").orElseThrow();
        User anotherUser = userRepository.save(new User("other_student", passwordEncoder.encode("Password@123"), "other@test.edu", Role.STUDENT, true));
        Student otherStudent = studentRepository.save(new Student(anotherUser, cseDept, "22CS999", 6, "9000000002", "Other Parent", "9000000003"));

        ApplicationCreateRequest request = new ApplicationCreateRequest(
                "Other request",
                "Going home",
                false,
                LocalDate.now(),
                LocalTime.now().withNano(0),
                LocalDateTime.now().plusHours(3)
        );

        ApplicationResponse app = applicationService.createApplication(otherStudent.getId(), request, null);

        // student1 attempts to cancel otherStudent's application
        Assertions.assertThrows(BadRequestException.class, () -> {
            applicationService.cancelApplication(app.getId(), studentId);
        });
    }
}
