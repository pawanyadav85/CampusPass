package com.campuspass;

import com.campuspass.dto.request.ApplicationCreateRequest;
import com.campuspass.dto.request.LoginRequest;
import com.campuspass.dto.response.ApplicationResponse;
import com.campuspass.dto.response.AuthResponse;
import com.campuspass.dto.response.MovementResponse;
import com.campuspass.dto.response.VerificationResponse;
import com.campuspass.entity.EPass;
import com.campuspass.entity.enums.ApplicationStatus;
import com.campuspass.entity.enums.MovementStatus;
import com.campuspass.entity.enums.PassStatus;
import com.campuspass.repository.EPassRepository;
import com.campuspass.repository.GateMovementRepository;
import com.campuspass.service.ApplicationService;
import com.campuspass.service.AuthService;
import com.campuspass.service.HodService;
import com.campuspass.service.SecurityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ApplicationWorkflowTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private HodService hodService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private EPassRepository ePassRepository;

    @Autowired
    private GateMovementRepository gateMovementRepository;

    @Test
    void testCompletePermissionAndGateLifecycle() {
        // 1. Authenticate as Student
        AuthResponse studentAuth = authService.login(new LoginRequest("student1", "Password@123"));
        Assertions.assertNotNull(studentAuth.getToken());
        Assertions.assertNotNull(studentAuth.getStudentId());

        // 2. Student Submits Permission Application
        LocalDate leaveDate = LocalDate.now();
        LocalTime leaveTime = LocalTime.now().withNano(0);
        LocalDateTime expectedReturn = LocalDateTime.now().plusHours(4);

        ApplicationCreateRequest appRequest = new ApplicationCreateRequest(
                "Bank Work",
                "I need to visit the local branch to complete KYC verification.",
                false,
                leaveDate,
                leaveTime,
                expectedReturn
        );

        ApplicationResponse appResponse = applicationService.createApplication(studentAuth.getStudentId(), appRequest, null);
        Assertions.assertNotNull(appResponse.getId());
        Assertions.assertEquals(ApplicationStatus.SUBMITTED, appResponse.getStatus());

        // 3. Authenticate as HOD
        AuthResponse hodAuth = authService.login(new LoginRequest("hod_cse", "Password@123"));
        Assertions.assertNotNull(hodAuth.getHodId());

        // 4. HOD Approves Application
        ApplicationResponse approvedApp = hodService.approveApplication(appResponse.getId(), hodAuth.getHodId(), "Approved for banking purposes");
        Assertions.assertEquals(ApplicationStatus.APPROVED, approvedApp.getStatus());

        // 5. Verify E-Pass Issued
        EPass pass = ePassRepository.findByApplicationId(approvedApp.getId()).orElse(null);
        Assertions.assertNotNull(pass);
        Assertions.assertEquals(PassStatus.ACTIVE, pass.getStatus());
        Assertions.assertNotNull(pass.getQrToken());

        // 6. Security Scans QR at Gate for Exit
        VerificationResponse exitVerification = securityService.verifyQrToken(pass.getQrToken());
        Assertions.assertTrue(exitVerification.isValid());
        Assertions.assertEquals("VALID_FOR_EXIT", exitVerification.getVerificationCode());

        // Authenticate as Security Guard
        AuthResponse secAuth = authService.login(new LoginRequest("security_gate1", "Password@123"));
        MovementResponse exitMovement = securityService.recordExit(secAuth.getSecurityUserId(), pass.getQrToken(), "Leaving on foot");
        Assertions.assertEquals(MovementStatus.OUTSIDE, exitMovement.getMovementStatus());

        // 7. Security Scans Same QR upon Return
        VerificationResponse returnVerification = securityService.verifyQrToken(pass.getQrToken());
        Assertions.assertTrue(returnVerification.isValid());
        Assertions.assertEquals("VALID_FOR_RETURN", returnVerification.getVerificationCode());

        MovementResponse returnMovement = securityService.recordReturn(secAuth.getSecurityUserId(), pass.getQrToken(), "Returned safely");
        Assertions.assertEquals(MovementStatus.RETURNED_ON_TIME, returnMovement.getMovementStatus());

        // 8. Verify Pass is Finalized to COMPLETED
        EPass finalPass = ePassRepository.findById(pass.getId()).orElseThrow();
        Assertions.assertEquals(PassStatus.COMPLETED, finalPass.getStatus());

        // 9. Re-scanning Completed Pass should fail with ALREADY_RETURNED
        VerificationResponse duplicateScan = securityService.verifyQrToken(pass.getQrToken());
        Assertions.assertFalse(duplicateScan.isValid());
        Assertions.assertEquals("ALREADY_RETURNED", duplicateScan.getVerificationCode());
    }
}
