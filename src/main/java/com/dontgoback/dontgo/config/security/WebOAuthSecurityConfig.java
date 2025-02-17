package com.example.demo.config.security;

import com.example.demo.config.jwt.TokenAuthenticationFilter;
import com.example.demo.config.jwt.TokenProvider;
import com.example.demo.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.demo.config.oauth.OAuth2SuccessHandler;
import com.example.demo.config.oauth.OAuth2UserCustomService;
import com.example.demo.domain.refreshToken.RefreshTokenRepository;
import com.example.demo.domain.user.service.UserService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){     // Spring Security 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // 토큰 방식 인증을 사용하기 때문에, 기존의 폼로그인과, 세션 방식 비활성화
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 비활성화
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 비활성화
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함
                );

            // 헤더를 확인할 커스텀 필터를 추가
            http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

            // 토큰 재발급 URL은 인증 없이 접근이 가능해야함
            // 나머지 API URL은 인증이 필요
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/token").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login") // 커스텀 로그인 페이지 설정
                .authorizationEndpoint(auth -> // 인증 요청 상태 저장
                        auth.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 실행할 핸들러
                .userInfoEndpoint(userInfo ->
                        userInfo.userService(oAuth2UserCustomService) // OAuth2 사용자 서비스 설정
                )
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/login") // 로그아웃 성공 후 이동할 URL 설정
        );

        // "/api"로 시작하는 url인 경우 401상태코드 반환하도록 예외 처리
        http.exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                )
        );

        return http.build();
    }


    //
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider, refreshTokenRepository, oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
        }

    @Bean
    public Filter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository
    oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }


}
