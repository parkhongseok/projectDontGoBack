package com.dontgoback.dontgo.domain.refreshToken;

import com.dontgoback.dontgo.global.util.CookieUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

import static com.dontgoback.dontgo.global.util.GlobalValues.*;

@RequiredArgsConstructor
@Service
public class TokenCookieService {

    @Value("${cookie.secure}")
    private boolean secureCookie;

    // ✨ 1. 로거(Logger) 추가
    private static final Logger log = LoggerFactory.getLogger(TokenCookieService.class);


    // 상수 정의
    private static final String ROOT_PATH = "/";
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(2); // TokenService의 만료시간과 일치


    public void addAccessTokenCookie(HttpServletResponse response, String accessToken) {
        int maxAge = (int) ACCESS_TOKEN_DURATION.toSeconds();

        // ✨ 2. 쿠키를 추가하기 직전에, secureCookie의 실제 값을 로그로 출력
        log.info("Adding access_token cookie with secure flag: {}", secureCookie);

        CookieUtil.addCookie(response, ACCESS_TOKEN_NAME, accessToken, maxAge, secureCookie, ROOT_PATH);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        int maxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        // ✨ 2. 쿠키를 추가하기 직전에, secureCookie의 실제 값을 로그로 출력
        log.info("Adding refresh_token cookie with secure flag: {}", secureCookie);

        CookieUtil.addCookie(response, REFRESH_TOKEN_NAME, refreshToken, maxAge, secureCookie, REFRESH_TOKEN_API_PATH);
    }

    // 필요 시 로그아웃 등 쿠키 삭제 메서드도 추가 가능
    public void clearTokenCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, ACCESS_TOKEN_NAME, secureCookie, "/");
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_NAME, secureCookie, REFRESH_TOKEN_API_PATH);
    }
}