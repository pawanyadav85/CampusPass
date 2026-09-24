package com.campuspass.config;

import com.campuspass.entity.*;
import com.campuspass.entity.enums.Role;
import com.campuspass.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;
    private final HodRepository hodRepository;
    private final SecurityUserRepository securityUserRepository;
    private final SystemSettingRepository systemSettingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            StudentRepository studentRepository,
            HodRepository hodRepository,
            SecurityUserRepository securityUserRepository,
            SystemSettingRepository systemSettingRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.studentRepository = studentRepository;
        this.hodRepository = hodRepository;
        this.securityUserRepository = securityUserRepository;
        this.systemSettingRepository = systemSettingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            seedSystemSettings();
            seedDepartments();
            seedUsers();
            logger.info("DataInitializer: Default data seeding verified successfully.");
        } catch (Exception ex) {
            logger.warn("DataInitializer skipped or database already initialized: {}", ex.getMessage());
        }
    }

    private void seedSystemSettings() {
        createSettingIfAbsent("GRACE_PERIOD_MINUTES", "15", "Allowed grace period in minutes after expected return time");
        createSettingIfAbsent("MAX_PASS_DURATION_HOURS", "48", "Maximum duration in hours for a student permission pass");
        createSettingIfAbsent("COLLEGE_NAME", "Apex Institute of Engineering & Technology", "Institutional name displayed on passes");
    }

    private void createSettingIfAbsent(String key, String value, String desc) {
        if (systemSettingRepository.findBySettingKey(key).isEmpty()) {
            systemSettingRepository.save(new SystemSetting(key, value, desc));
        }
    }

    private void seedDepartments() {
        createDepartmentIfAbsent("CSE", "Computer Science & Engineering", "Department of Computer Science");
        createDepartmentIfAbsent("IT", "Information Technology", "Department of Information Technology");
        createDepartmentIfAbsent("ECE", "Electronics & Communication", "Department of Electronics");
        createDepartmentIfAbsent("MECH", "Mechanical Engineering", "Department of Mechanical Engineering");
    }

    private Department createDepartmentIfAbsent(String code, String name, String desc) {
        return departmentRepository.findByCode(code).orElseGet(() -> {
            Department d = new Department(null, code, name, desc, true);
            return departmentRepository.save(d);
        });
    }

    private void seedUsers() {
        String defaultPasswordHash = passwordEncoder.encode("Password@123");

        // 1. Admin
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", defaultPasswordHash, "admin@campuspass.edu", Role.ADMIN, true);
            userRepository.save(admin);
            logger.info("Seeded Admin: admin / Password@123");
        }

        // 2. HOD CSE
        Department cse = departmentRepository.findByCode("CSE").orElse(null);
        if (userRepository.findByUsername("hod_cse").isEmpty() && cse != null) {
            User hodUser = new User("hod_cse", defaultPasswordHash, "hod.cse@campuspass.edu", Role.HOD, true);
            hodUser = userRepository.save(hodUser);

            Hod hod = new Hod(hodUser, cse, "EMP-CSE-001", "Room 302, CS Block", "9876543210");
            hodRepository.save(hod);
            logger.info("Seeded HOD CSE: hod_cse / Password@123");
        }

        // 3. Security Guard
        if (userRepository.findByUsername("security_gate1").isEmpty()) {
            User secUser = new User("security_gate1", defaultPasswordHash, "gate1@campuspass.edu", Role.SECURITY, true);
            secUser = userRepository.save(secUser);

            SecurityUser sec = new SecurityUser(secUser, "SEC-001", "Main North Gate", "9876543211");
            securityUserRepository.save(sec);
            logger.info("Seeded Security: security_gate1 / Password@123");
        }

        // 4. Student
        if (userRepository.findByUsername("student1").isEmpty() && cse != null) {
            User studentUser = new User("student1", defaultPasswordHash, "student1@campuspass.edu", Role.STUDENT, true);
            studentUser = userRepository.save(studentUser);

            Student student = new Student(
                    studentUser,
                    cse,
                    "22CS101",
                    6,
                    "9876543212",
                    "Rajesh Kumar",
                    "9876543213"
            );
            studentRepository.save(student);
            logger.info("Seeded Student: student1 / Password@123");
        }
    }
}
