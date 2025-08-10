package com.dontgoback.dontgo.config.security;

import com.dontgoback.dontgo.config.jwt.TokenAuthenticationFilter;
import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.dontgoback.dontgo.config.oauth.OAuth2SuccessHandler;
import com.dontgoback.dontgo.config.oauth.OAuth2UserCustomService;
import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenRepository;
import com.dontgoback.dontgo.domain.user.UserService;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Profile("dev")
@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfigDev {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${app.FRONTEND_URL}")
    private String FRONTEND_URL;

    @Bean
    public WebSecurityCustomizer configure() {     // Spring Security 기능 비활성화
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    // 1. 공개(Public) 엔드포인트를 위한 필터 체인
    // - 가장 먼저 실행되도록 @Order(1) 설정
    // - TokenAuthenticationFilter가 포함되지 않은 매우 가벼운 체인
    @Bean
    @Order(1)
    public SecurityFilterChain publicEndpointsFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher( // 이 필터 체인이 처리할 경로들을 명시적으로 지정
                        "/test/internal/batch/asset-refresh/run",
                        "/api/token",
                        "/api/logout",
                        "/api/v1/users/account-close",
                        "/api/v1/users/account-inactive"
                )
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모든 요청 허용
                .csrf(AbstractHttpConfigurer::disable); // 간단한 API이므로 CSRF 비활성화
        return http.build();
    }

    // 2. 인증이 필요한 핵심 API 및 OAuth를 위한 메인 필터 체인
    @Bean
    @Order(2) // 공개 엔드포인트 필터 체인 다음에 실행
    public SecurityFilterChain mainFilterChain(HttpSecurity http) throws Exception {
        // 토큰 방식 인증을 사용하기 때문에, 기존의 폼로그인과, 세션 방식 비활성화
        http
                .cors(cors -> cors.configurationSource(
                        // CORS 설정 적용 (아래 메서드 참조)
                        corsConfigurationSource())
                )
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP Basic 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // Form 로그인 비활성화
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함
                );

        // 헤더를 확인할 커스텀 필터를 추가 (헤더에서 유저 토큰 뜯어서 유효하다면, 이제 시큐리티 상에서 인증된 유저로 취급)
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 인증이 필요한 API 요청에서 401 반환
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
        );

        // OAuth 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                // 사용자 인증이 필요하거나, 인증 실패 시, 연결되는 로그인 페이지
                .loginPage(FRONTEND_URL + "/login")
                // 인증 요청 상태 저장 : 인증 요청을 시작한 뒤, 리다이렉션을 통해 돌아올 때 그 상태를 복원
                .authorizationEndpoint(auth -> auth
                        .baseUri("/api/oauth2/authorization") // 기본 경로 변경
                        // 인증 과정에서 필요한 레퍼지토리를 등록
                        .authorizationRequestRepository(
                                oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                // 리다이렉션 URL 변경 (기본: /login/oauth2/code/google)
                .redirectionEndpoint(redir -> redir
                        .baseUri("/api/login/oauth2/code/*") // 리다이렉트 경로 변경 개발환경에서도 작동하도록
                )
                // 성공적으로 완료된 경우 실행될 핸들러
                .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 실행할 핸들러 호출 (커스텀 핸들러 호출)
                .userInfoEndpoint(userInfo ->
                        userInfo.userService(oAuth2UserCustomService) // OAuth2 사용자 서비스 설정
                )
        );

        // publicEndpointsFilterChain에서 처리되지 않은 모든 요청은 이 체인으로 들어오게 됨
        // 따라서 여기서는 모든 요청에 대해 인증을 요구하도록 단순화할 수 있음
        http.authorizeHttpRequests(auth -> auth.anyRequest().authenticated());
        return http.build();
    }

    // CORS 처리를 위한 필터
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(FRONTEND_URL));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        // OPTIONS: 브라우저의 사전 요청(Preflight 요청) 허용 cors 사전 조사
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
    

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
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}