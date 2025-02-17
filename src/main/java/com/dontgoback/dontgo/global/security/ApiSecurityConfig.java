package com.dontgoback.dontgo.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http)  throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/api/*/feeds").permitAll() // 전체 글보기
                                .requestMatchers("/api/*/feeds/*").permitAll() // 게시글 보기
                                .requestMatchers(HttpMethod.POST, "/api/*/users/login").permitAll() // 로그인 페이지
//                                .requestMatchers("/api/*/users/*").permitAll()
                                .anyRequest().authenticated() // 위 이외에는 모두 인증/인가 처리된 사용자만 허용하겠다
                )
                .cors(
                        AbstractHttpConfigurer::disable
//                        cors -> cors.disable()
                )
                // jwt 로그인 방식을 제외한 다른 방식 끄기
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .httpBasic(
                        AbstractHttpConfigurer::disable
//                        httpBasic -> httpBasic.disable()
                ).formLogin(
                        AbstractHttpConfigurer::disable
                // 세선 끄기
                ).sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // 시큐리티 필터 전에, JWT 검증
                ).addFilterBefore(
                        jwtAuthorizationFilter, // 엑세스 토큰을 이용한 로그인 처리
                        UsernamePasswordAuthenticationFilter.class
                )
        ;
        return http.build();
    }
}
