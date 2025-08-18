package com.dontgoback.dontgo.interserver.auth.jwt;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * JWT를 발급 및 캐싱하여 재사용하고,
 * 만료 시 자동으로 재발급 요청을 수행하는 매니저입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterServerTokenManager {

    private final InterServerJwtRequestService jwtRequestService;
    private final Clock clock;
    private InterServerTokenHolder holder = null;           // 메모리에 저장된 현재 토큰 홀더

    /**
     * 유효한 JWT를 반환합니다.
     * - 최초 요청 시
     * - 또는 기존 토큰이 만료된 경우 새로 발급 요청
     *
     * @return JWT 문자열
     */
    public String getToken() {
        if (holder == null || holder.isExpired(clock)) {
            log.info("JWT 없음 또는 만료됨 → 새로 발급 요청");
            String newToken = jwtRequestService.requestJwt();
            if (newToken == null) {
                log.error("인증 서버로부터 JWT를 발급받을 수 없습니다.");
                throw new IllegalStateException("JWT 발급 실패");
            }
            this.holder = new InterServerTokenHolder(newToken, LocalDateTime.now(clock));
        }

        return holder.getToken();
    }

    /**
     * 강제로 기존 토큰을 폐기하고 재발급 요청합니다.
     * - 예: 확장 서버로부터 401 응답을 받은 경우 호출
     *
     * @return 새로 발급된 JWT 문자열
     */
    public String forceRefresh() {
        log.info("토큰 강제 재발급 시작");
        String newToken = jwtRequestService.requestJwt();
        if (newToken == null) {
            log.error("JWT 강제 재발급 실패");
            throw new IllegalStateException("JWT 재발급 실패");
        }
        this.holder = new InterServerTokenHolder(newToken, LocalDateTime.now(clock));
        return newToken;
    }
}
