package com.dontgoback.dontgo.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    @Override
    @SneakyThrows
    // 인가 작업
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        if (request.getRequestURI().equals("/api/v1/users/login") || request.getRequestURI().equals("/api/v1/users/logout")){
            filterChain.doFilter(request, response);
            return ;
        }

        // accessToken 검증 or refreshToken 발급
        String accessToken = "";
        if (!accessToken.isBlank()) {

        }
        filterChain.doFilter(request, response);


    }

}
