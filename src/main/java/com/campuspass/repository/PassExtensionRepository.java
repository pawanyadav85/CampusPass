package com.campuspass.repository;

import com.campuspass.entity.PassExtension;
import com.campuspass.entity.enums.ExtensionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PassExtensionRepository extends JpaRepository<PassExtension, Long> {

    List<PassExtension> findByPassIdOrderByCreatedAtDesc(Long passId);

    Optional<PassExtension> findFirstByPassIdAndStatusOrderByCreatedAtDesc(Long passId, ExtensionStatus status);

    @Query("SELECT pe FROM PassExtension pe WHERE pe.student.department.id = :deptId AND pe.status = :status ORDER BY pe.createdAt DESC")
    List<PassExtension> findByDepartmentAndStatus(
            @Param("deptId") Long deptId,
            @Param("status") ExtensionStatus status);
}
