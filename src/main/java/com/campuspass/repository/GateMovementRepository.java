package com.campuspass.repository;

import com.campuspass.entity.GateMovement;
import com.campuspass.entity.enums.MovementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GateMovementRepository extends JpaRepository<GateMovement, Long> {

    Optional<GateMovement> findFirstByPassIdOrderByCreatedAtDesc(Long passId);

    Optional<GateMovement> findFirstByStudentIdAndMovementStatusInOrderByCreatedAtDesc(
            Long studentId, List<MovementStatus> statuses);

    List<GateMovement> findByMovementStatusOrderByExitTimeDesc(MovementStatus status);

    List<GateMovement> findByMovementStatusInOrderByExitTimeDesc(List<MovementStatus> statuses);

    @Query("SELECT gm FROM GateMovement gm WHERE gm.student.department.id = :deptId AND gm.movementStatus IN :statuses ORDER BY gm.exitTime DESC")
    List<GateMovement> findByDepartmentAndMovementStatuses(
            @Param("deptId") Long deptId,
            @Param("statuses") List<MovementStatus> statuses);

    @Query("SELECT COUNT(gm) FROM GateMovement gm WHERE gm.student.department.id = :deptId AND gm.movementStatus = :status")
    long countByDepartmentAndStatus(
            @Param("deptId") Long deptId,
            @Param("status") MovementStatus status);

    long countByMovementStatus(MovementStatus status);

    @Query("SELECT gm FROM GateMovement gm WHERE gm.movementStatus = 'OUTSIDE' AND gm.expectedReturnTime < :cutoffTime")
    List<GateMovement> findOverdueMovements(@Param("cutoffTime") LocalDateTime cutoffTime);
}
