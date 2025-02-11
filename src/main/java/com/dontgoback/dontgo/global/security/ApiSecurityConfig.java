package com.dontgoback.dontgo.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http)  throws Exception {
        http
                .securityMatcher("/api/**")
                .authorizeHttpRequests(
                        authorizeRequests -> authorizeRequests
                                .requestMatchers("/api/*/feeds").permitAll()
                                .requestMatchers("/api/*/feeds/*").permitAll()
//                                .requestMatchers("/api/*/users/*").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(
                        AbstractHttpConfigurer::disable);
        return http.build();
    }
}
