package com.campuspass.repository;

import com.campuspass.entity.EPass;
import com.campuspass.entity.enums.PassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EPassRepository extends JpaRepository<EPass, Long> {

    Optional<EPass> findByQrToken(String qrToken);

    Optional<EPass> findByPassNumber(String passNumber);

    Optional<EPass> findByApplicationId(Long applicationId);

    Optional<EPass> findFirstByStudentIdAndStatusOrderByIssuedAtDesc(Long studentId, PassStatus status);

    List<EPass> findByStudentIdOrderByIssuedAtDesc(Long studentId);

    @Query("SELECT p FROM EPass p WHERE p.status = 'ACTIVE' AND p.validUntil < :now")
    List<EPass> findExpiredActivePasses(@Param("now") LocalDateTime now);
}
