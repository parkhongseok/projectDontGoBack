//package com.dontgoback.dontgo.domain.user;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.NoOpPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//@RequiredArgsConstructor
//@Service
//public class UserDetailService implements UserDetailsService {
//    private final UserRepository userRepository;
//
//    // 스프링 시큐리티에서 사용자 정보를 가져오는 인터페이스
//    @Override
//    public User loadUserByUsername(String email){
//        return userRepository.findByEmail(email)
//                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 실패. 올바른 사용자 정보가 아닙니다."+email));
//    }
//}
