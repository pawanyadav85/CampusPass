package com.campuspass.scheduler;

import com.campuspass.entity.EPass;
import com.campuspass.entity.GateMovement;
import com.campuspass.entity.Hod;
import com.campuspass.entity.enums.MovementStatus;
import com.campuspass.entity.enums.PassStatus;
import com.campuspass.repository.*;
import com.campuspass.service.AuditLogService;
import com.campuspass.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OverdueScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OverdueScheduler.class);

    private final GateMovementRepository gateMovementRepository;
    private final EPassRepository ePassRepository;
    private final HodRepository hodRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final int defaultGracePeriodMinutes;

    public OverdueScheduler(
            GateMovementRepository gateMovementRepository,
            EPassRepository ePassRepository,
            HodRepository hodRepository,
            SystemSettingRepository systemSettingRepository,
            NotificationService notificationService,
            AuditLogService auditLogService,
            @Value("${campuspass.defaults.grace-period-minutes:15}") int defaultGracePeriodMinutes) {
        this.gateMovementRepository = gateMovementRepository;
        this.ePassRepository = ePassRepository;
        this.hodRepository = hodRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
        this.defaultGracePeriodMinutes = defaultGracePeriodMinutes;
    }

    /**
     * Runs every 60 seconds to detect students who have exceeded their return time + grace period.
     */
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void evaluateOverdueStudents() {
        int graceMinutes = systemSettingRepository.findBySettingKey("GRACE_PERIOD_MINUTES")
                .map(s -> {
                    try {
                        return Integer.parseInt(s.getSettingValue());
                    } catch (NumberFormatException e) {
                        return defaultGracePeriodMinutes;
                    }
                }).orElse(defaultGracePeriodMinutes);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoffTime = now.minusMinutes(graceMinutes);

        List<GateMovement> overdueCandidates = gateMovementRepository.findOverdueMovements(cutoffTime);
        if (!overdueCandidates.isEmpty()) {
            logger.info("Found {} students overdue beyond grace period of {} minutes", overdueCandidates.size(), graceMinutes);
        }

        for (GateMovement movement : overdueCandidates) {
            movement.setMovementStatus(MovementStatus.OVERDUE);
            gateMovementRepository.save(movement);

            // Alert student
            notificationService.createNotification(
                    movement.getStudent().getUser().getId(),
                    "⚠️ OVERDUE ALERT: Return Time Exceeded",
                    "Your expected return time was " + movement.getExpectedReturnTime().toLocalTime().withNano(0) +
                            ". The " + graceMinutes + "-minute grace period has elapsed. Please return immediately or submit an extension request.",
                    "OVERDUE_ALERT",
                    movement.getId()
            );

            // Alert departmental HOD
            Hod hod = hodRepository.findByDepartmentId(movement.getStudent().getDepartment().getId()).orElse(null);
            if (hod != null) {
                notificationService.createNotification(
                        hod.getUser().getId(),
                        "⚠️ Overdue Student Alert: " + movement.getStudent().getRollNumber(),
                        "Student " + movement.getStudent().getUser().getUsername() + " (" +
                                movement.getStudent().getRollNumber() + ") is currently OVERDUE.",
                        "HOD_OVERDUE_ALERT",
                        movement.getId()
                );
            }

            auditLogService.log(
                    null,
                    "SYSTEM_SCHEDULER",
                    "SYSTEM",
                    "OVERDUE_TRIGGERED",
                    "GateMovement",
                    movement.getId(),
                    "Movement marked OVERDUE for student: " + movement.getStudent().getRollNumber()
            );
        }
    }

    /**
     * Runs every 5 minutes to expire unutilized passes whose validity window has elapsed.
     */
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void expireUnutilizedPasses() {
        LocalDateTime now = LocalDateTime.now();
        List<EPass> expiredPasses = ePassRepository.findExpiredActivePasses(now);

        for (EPass pass : expiredPasses) {
            // Verify if student exited
            boolean hasExited = gateMovementRepository.findFirstByPassIdOrderByCreatedAtDesc(pass.getId()).isPresent();
            if (!hasExited) {
                pass.setStatus(PassStatus.EXPIRED);
                ePassRepository.save(pass);

                notificationService.createNotification(
                        pass.getStudent().getUser().getId(),
                        "Pass Expired",
                        "Your pass " + pass.getPassNumber() + " expired without utilization.",
                        "PASS_EXPIRED",
                        pass.getId()
                );

                auditLogService.log(
                        null,
                        "SYSTEM_SCHEDULER",
                        "SYSTEM",
                        "PASS_EXPIRED",
                        "EPass",
                        pass.getId(),
                        "Unutilized pass marked EXPIRED: " + pass.getPassNumber()
                );
            }
        }
    }
}
