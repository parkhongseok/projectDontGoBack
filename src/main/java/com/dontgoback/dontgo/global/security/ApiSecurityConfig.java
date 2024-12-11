//package com.dontgoback.dontgo.global.security;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class ApiSecurityConfig {
//
//    @Bean
//    SecurityFilterChain filterChain(HttpSecurity http)  throws Exception {
//        http
//                .securityMatcher("/api/**")
//                .authorizeHttpRequests(
//                        authorizeRequests -> authorizeRequests
//                                .requestMatchers("/api/*/articles/**").permitAll()
//                                .anyRequest().permitAll()
//                )
//                .csrf(
//                csrf -> csrf.disable());
//        return http.build();
//    }
//}
