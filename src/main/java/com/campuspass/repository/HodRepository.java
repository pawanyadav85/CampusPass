package com.campuspass.repository;

import com.campuspass.entity.Department;
import com.campuspass.entity.Hod;
import com.campuspass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HodRepository extends JpaRepository<Hod, Long> {
    Optional<Hod> findByUser(User user);
    Optional<Hod> findByUserId(Long userId);
    Optional<Hod> findByDepartment(Department department);
    Optional<Hod> findByDepartmentId(Long departmentId);
    Optional<Hod> findByEmployeeId(String employeeId);
}
