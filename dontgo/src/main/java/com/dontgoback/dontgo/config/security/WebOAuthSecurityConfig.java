package com.dontgoback.dontgo.config.security;

import com.dontgoback.dontgo.config.jwt.TokenAuthenticationFilter;
import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.dontgoback.dontgo.config.oauth.OAuth2SuccessHandler;
import com.dontgoback.dontgo.config.oauth.OAuth2UserCustomService;
import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenRepository;
import com.dontgoback.dontgo.domain.user.UserService;
import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
//                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
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
                .loginPage("http://localhost:3000/login")
                // 인증 요청 상태 저장 : 인증 요청을 시작한 뒤, 리다이렉션을 통해 돌아올 때 그 상태를 복원
                .authorizationEndpoint(auth ->
                        auth.authorizationRequestRepository(
                                oAuth2AuthorizationRequestBasedOnCookieRepository())
                )
                // 성공적으로 완료된 경우 실행될 핸들러
                .successHandler(oAuth2SuccessHandler()) // 인증 성공 시 실행할 핸들러 호출 (커스텀 핸들러 호출)
                .userInfoEndpoint(userInfo ->
                        userInfo.userService(oAuth2UserCustomService) // OAuth2 사용자 서비스 설정
                )
        );


        // 위에서 인증 실패 시, 인증을 다시 요청해야함 이때, 인증 관련 api 자체는 인증없이 접근 가능하도록 설정하고
        // 이외의 모든 페이지는 인증되지 않으면 401 에러 반환
        // 토큰 재발급 URL은 인증 없이 접근이 가능해야함
        // 나머지 API URL은 인증이 필요
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/token").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/logout").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        );
        return http.build();
    }

    // CORS 처리를 위한 필터
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE","OPTIONS"));// OPTIONS: 브라우저의 사전 요청(Preflight 요청) 허용
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
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}