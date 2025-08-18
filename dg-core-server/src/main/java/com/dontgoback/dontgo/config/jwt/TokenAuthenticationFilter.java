package com.dontgoback.dontgo.config.jwt;

import com.dontgoback.dontgo.global.util.CookieUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.dontgoback.dontgo.global.util.GlobalValues.ACCESS_TOKEN_NAME;

// 로그인 후 인가 처리
// 헤더 확인을 위해 추가한 커스텀 필터로, 세션, 폼 로그인 비활성화 이후 실행
/** OncePerRequestFilter 재발 방지 체크리스트
 *  화이트리스트면 즉시 통과(return).
 *  토큰 유효 → 컨텍스트 세팅 → chain.doFilter → 컨텍스트 clear.
 *  토큰 없음/무효 → 바로 401 후 return(체인 태우지 않음).
 *  finally에서 응답 상태를 바꾸지 않음.
 *  RequestMatcher(예: AntPathRequestMatcher)로 경로 매칭 일원화.
 *  SessionCreationPolicy.STATELESS 설정 유지.
 *  필터 순서: UsernamePasswordAuthenticationFilter보다 앞.*/
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final List<RequestMatcher> whiteListMatchers;

    public TokenAuthenticationFilter(TokenProvider tokenProvider, String... publicUrls) {
        this.tokenProvider = tokenProvider;
        this.whiteListMatchers = Arrays.stream(publicUrls)
                .map(AntPathRequestMatcher::new)
                .map(m -> (RequestMatcher) m)   // 업캐스팅
                .toList();                      // List<RequestMatcher>

    }

    private boolean isWhitelisted(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true; // Preflight
        return whiteListMatchers.stream().anyMatch(m -> m.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 1) 화이트리스트는 인증 검사 없이 통과
        if (isWhitelisted(req)) {
            chain.doFilter(req, res);
            return;
        }

        // 2) 접근 토큰 검사
        String token = getAccessToken(req);
        log.warn("[SEC] access_token(prefix)={}", token != null ? token.substring(0, 16) : "null");


        // 2-1) 토큰이 유효하면 인증 세팅 후 통과
        if (token != null && tokenProvider.isTokenValid(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            try {
                chain.doFilter(req, res);
            } finally {
                SecurityContextHolder.clearContext(); // 누수 방지
            }
            return;
        }

        // 2-2) 토큰 없음/무효 → 여기서 즉시 401로 종료 (체인 태우지 않음)
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 필요시 바디/헤더 추가:
        // res.setContentType("application/json");
        // res.getWriter().write("{\"code\":\"401\",\"message\":\"Unauthorized\"}");
    }

    private String getAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        return CookieUtil.findCookie(cookies, ACCESS_TOKEN_NAME).map(Cookie::getValue).orElse(null);
    }

}