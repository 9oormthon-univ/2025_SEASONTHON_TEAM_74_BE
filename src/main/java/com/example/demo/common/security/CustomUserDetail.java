package com.example.demo.common.security;

import com.example.demo.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class CustomUserDetail implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 권한 관리가 없으므로 null 또는 빈 리스트 반환
    }

    @Override
    public String getUsername() {
        return String.valueOf(user.getId());
    }

    @Override
    public String getPassword() {
        return "no pwd"; // 비밀번호는 사용하지 않으므로 임의의 문자열 반환
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
        return true;
    }
}
