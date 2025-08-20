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
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dontgoback.dontgo.global.util.GlobalValues.REFRESH_TOKEN_API_PATH;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET) // ✅ 서블릿 웹일 때만
@Profile("!seed") // 선택: seed에선 아예 제외
@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final TokenCookieService tokenCookieService;
    private final RefreshTokenService refreshTokenService;
    private final String AUTHORIZATION_REQUEST_BASE_URL = "/api/oauth2/authorization";
    private final String AUTHORIZATION_RESPONSE_BASE_URL = "/api/login/oauth2/code/*";
    private final ClientRegistrationRepository clientRegistrationRepository;
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
                                .baseUri(AUTHORIZATION_REQUEST_BASE_URL)
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                                .authorizationRequestResolver(customAuthorizationRequestResolver())
                        )

                        .redirectionEndpoint(redir -> redir
                                .baseUri(AUTHORIZATION_RESPONSE_BASE_URL)
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


    /**
     * ✨ Google 로그인 시 항상 계정 선택 창을 표시하도록 authorizationRequestResolver를 커스터마이징
     */
    @Bean
    public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                this.clientRegistrationRepository, "/api/oauth2/authorization"
        );
        // Standard OAuth2 request resolver를 기반으로 하되, 추가 파라미터를 설정
        resolver.setAuthorizationRequestCustomizer(customizer ->
                customizer.additionalParameters(params -> params.put("prompt", "select_account"))
        );
        return resolver;
    }

//    // clientRegistrationRepository() 빈이 없다면 추가해야 합니다.
//    // 보통 Spring Boot가 자동 설정하지만, 명시적으로 필요할 수 있습니다.
//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        // application.yml/properties의 spring.security.oauth2.client.registration... 설정을 기반으로 자동 생성됩니다.
//        // 만약 직접 ClientRegistration을 빌드했다면 해당 로직을 여기에 넣어야 합니다.
//        // 일반적으로는 주입받아서 사용하면 됩니다. 아래는 예시입니다.
//        // 이 빈을 SecurityConfig의 필드로 주입받도록 변경하는 것이 더 좋습니다.
//        return new InMemoryClientRegistrationRepository(/* ... */); // 실제 환경에서는 주입받아서 사용
//    }
}