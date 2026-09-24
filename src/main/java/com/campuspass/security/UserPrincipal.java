package com.campuspass.security;

import com.campuspass.entity.User;
import com.campuspass.entity.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final String password;
    private final Role role;
    private final Long departmentId;
    private final Long studentId;
    private final Long hodId;
    private final Long securityUserId;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String username, String email, String password, Role role,
                         Long departmentId, Long studentId, Long hodId, Long securityUserId,
                         boolean active, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.departmentId = departmentId;
        this.studentId = studentId;
        this.hodId = hodId;
        this.securityUserId = securityUserId;
        this.active = active;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user, Long departmentId, Long studentId, Long hodId, Long securityUserId) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                departmentId,
                studentId,
                hodId,
                securityUserId,
                user.isActive(),
                Collections.singletonList(authority)
        );
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public Long getHodId() {
        return hodId;
    }

    public Long getSecurityUserId() {
        return securityUserId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
