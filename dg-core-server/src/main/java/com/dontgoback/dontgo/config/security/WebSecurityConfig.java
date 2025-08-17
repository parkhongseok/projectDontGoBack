package com.dontgoback.dontgo.config.security;

import com.dontgoback.dontgo.config.jwt.TokenAuthenticationFilter;
import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.dontgoback.dontgo.config.oauth.OAuth2SuccessHandler;
import com.dontgoback.dontgo.config.oauth.OAuth2UserCustomService;
import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenService;
import com.dontgoback.dontgo.domain.refreshToken.TokenCookieService;
import com.dontgoback.dontgo.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.dontgoback.dontgo.global.util.GlobalValues.REFRESH_TOKEN_API_PATH;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final TokenCookieService tokenCookieService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.FRONTEND_URL}")
    private String FRONTEND_URL;

    /**
     * ✨ [분리] 개발 환경(dev)에서만 적용될 보안 설정을 위한 중첩 클래스
     */
    @Configuration
    @Profile("dev")
    public static class DevSpecificConfig {

        // H2 콘솔 접속을 위한 필터 체인 (최우선 순위)
        @Bean
        @Order(0)
        public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
            return http
                    .securityMatcher(toH2Console())
                    .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()))
                    .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                    .build();
        }

        // 정적 리소스에 대한 보안 비활성화
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring()
                    .requestMatchers("/img/**", "/css/**", "/js/**");
        }
    }

    /**
     * ✨ [통합] 애플리케이션의 메인 보안 규칙을 정의하는 단일 필터 체인
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 공개할 API 경로들을 명확하게 정의
        final String[] PUBLIC_URLS = {
                "/test/**",
                REFRESH_TOKEN_API_PATH, // "/api/token"
                "/api/guest"
        };

        // 1. 공통 설정 (CORS, CSRF, 세션 관리 등)
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 2. 경로별 인가(Authorization) 규칙 설정
        http
                .authorizeHttpRequests(auth -> auth
                        /** Spring Boot의 에러 처리 개입:
                         * Spring Boot는 이 404 예외를 감지하고, 클라이언트에게 보여줄 기본 에러 응답(JSON 또는 HTML)을 생성하려고 합니다.
                         * 이를 위해 내부적으로 요청을 /error 라는 특수한 경로로 포워딩(forward)합니다.
                         */
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/guest").permitAll()
                        .requestMatchers(PUBLIC_URLS).permitAll() // 기본 공개 API 경로들
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/account-close").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/account-inactive").permitAll()
                        .anyRequest().authenticated() // 나머지 모든 요청은 인증 필요
                );

        // 3. OAuth2 로그인 설정
        http
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(FRONTEND_URL + "/login")
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/api/oauth2/authorization")
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                        )
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/api/login/oauth2/code/*")
                        )
                        .successHandler(oAuth2SuccessHandler())
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
                );

        // 4. JWT 토큰 필터 추가 (공개 URL 목록 전달)
        http.addFilterBefore(
                new TokenAuthenticationFilter(tokenProvider, PUBLIC_URLS),
                UsernamePasswordAuthenticationFilter.class
        );

        // 5. 인증/인가 예외 처리
        http
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }
    // --- 나머지 Helper Bean 설정들은 변경 없이 그대로 유지 ---

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(FRONTEND_URL));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenCookieService, refreshTokenService,
                tokenProvider, oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService);
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