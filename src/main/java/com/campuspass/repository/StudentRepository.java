package com.campuspass.repository;

import com.campuspass.entity.Student;
import com.campuspass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUser(User user);
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByRollNumber(String rollNumber);
    boolean existsByRollNumber(String rollNumber);
    List<Student> findByDepartmentId(Long departmentId);
}
