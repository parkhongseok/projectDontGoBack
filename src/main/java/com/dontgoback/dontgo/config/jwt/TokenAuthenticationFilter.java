package com.example.demo.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // 가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);
        // 가져온 토큰이 유효한지 확인 후, 유효한 경우 인증 정보 설정
        if (tokenProvider.validToken(token)) {
            // getAuthentication메서드는 USER(security) 객체 반환
            // 여기엔 유저 이름과 권한 목록 등의 정보가 포함
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 키가 'Authorization'인 필드의 값을 갖고와서,
    // 접두사인 Bearer를 제외한 값을 얻음
    // 만약 값이 null이거나 Beare로 시작하지 않으면 null 반환
    private String getAccessToken(String authorizationHeader){
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
