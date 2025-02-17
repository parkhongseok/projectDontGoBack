package com.example.demo.domain.user.service;

import com.example.demo.domain.user.entity.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    // 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스를 여기서 직접 구현
    // 필수 구현 메서드 오버라이딩
    @Override
    public User loadUserByUsername(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException(email));
    }

}
