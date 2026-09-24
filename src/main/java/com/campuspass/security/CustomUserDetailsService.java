package com.campuspass.security;

import com.campuspass.entity.Hod;
import com.campuspass.entity.SecurityUser;
import com.campuspass.entity.Student;
import com.campuspass.entity.User;
import com.campuspass.repository.HodRepository;
import com.campuspass.repository.SecurityUserRepository;
import com.campuspass.repository.StudentRepository;
import com.campuspass.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final HodRepository hodRepository;
    private final SecurityUserRepository securityUserRepository;

    public CustomUserDetailsService(
            UserRepository userRepository,
            StudentRepository studentRepository,
            HodRepository hodRepository,
            SecurityUserRepository securityUserRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.hodRepository = hodRepository;
        this.securityUserRepository = securityUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Long departmentId = null;
        Long studentId = null;
        Long hodId = null;
        Long securityUserId = null;

        switch (user.getRole()) {
            case STUDENT -> {
                Student student = studentRepository.findByUserId(user.getId()).orElse(null);
                if (student != null) {
                    studentId = student.getId();
                    departmentId = student.getDepartment().getId();
                }
            }
            case HOD -> {
                Hod hod = hodRepository.findByUserId(user.getId()).orElse(null);
                if (hod != null) {
                    hodId = hod.getId();
                    departmentId = hod.getDepartment().getId();
                }
            }
            case SECURITY -> {
                SecurityUser securityUser = securityUserRepository.findByUserId(user.getId()).orElse(null);
                if (securityUser != null) {
                    securityUserId = securityUser.getId();
                }
            }
            case ADMIN -> {
                // Admins have campus-wide access
            }
        }

        return UserPrincipal.create(user, departmentId, studentId, hodId, securityUserId);
    }
}
