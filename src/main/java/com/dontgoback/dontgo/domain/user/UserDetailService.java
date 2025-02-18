package com.dontgoback.dontgo.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    // 구글 로그인만 사용하는 상태로, 따로 비밀번호 사용 안함
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // 비밀번호 검증을 하지 않음
    }

    // 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스를 여기서 직접 구현
    // 필수 구현 메서드 오버라이딩
    @Override
    public User loadUserByUsername(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException(email));
    }
}
