package com.campuspass.repository;

import com.campuspass.entity.Application;
import com.campuspass.entity.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByApplicationNumber(String applicationNumber);

    List<Application> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    Page<Application> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);

    List<Application> findByDepartmentIdOrderByCreatedAtDesc(Long departmentId);

    List<Application> findByDepartmentIdAndStatusOrderByCreatedAtDesc(Long departmentId, ApplicationStatus status);

    long countByDepartmentIdAndStatus(Long departmentId, ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.department.id = :deptId AND a.status = :status AND a.updatedAt BETWEEN :startOfDay AND :endOfDay")
    long countByDepartmentAndStatusToday(
            @Param("deptId") Long deptId,
            @Param("status") ApplicationStatus status,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT a FROM Application a WHERE a.department.id = :deptId AND (:status IS NULL OR a.status = :status) ORDER BY a.emergency DESC, a.createdAt DESC")
    List<Application> findDepartmentApplicationsWithEmergencyPriority(
            @Param("deptId") Long deptId,
            @Param("status") ApplicationStatus status
    );
}
