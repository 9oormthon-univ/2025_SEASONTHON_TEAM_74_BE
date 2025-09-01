package com.example.demo.common.security;

import com.example.demo.apiPayload.code.exception.GeneralException;
import com.example.demo.apiPayload.code.status.ErrorStatus;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userPk)  {
        User user = userRepository.findById(Long.parseLong(userPk))
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return new CustomUserDetail(user);
    }	// 위에서 생성한 CustomUserDetails Class
}

