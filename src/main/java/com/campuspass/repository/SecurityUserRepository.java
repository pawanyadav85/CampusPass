package com.campuspass.repository;

import com.campuspass.entity.SecurityUser;
import com.campuspass.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {
    Optional<SecurityUser> findByUser(User user);
    Optional<SecurityUser> findByUserId(Long userId);
    Optional<SecurityUser> findByBadgeId(String badgeId);
}
