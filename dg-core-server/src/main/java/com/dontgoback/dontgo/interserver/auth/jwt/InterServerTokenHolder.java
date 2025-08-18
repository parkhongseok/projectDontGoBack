package com.dontgoback.dontgo.interserver.auth.jwt;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * 메모리에 JWT와 발급 시점을 보관하는 역할을 수행합니다.
 * 만료 여부 확인을 통해 재발급이 필요한지 판단할 수 있습니다.
 */
public class InterServerTokenHolder {

    private final String token;
    private final LocalDateTime issuedAt;
    private static final long EXPIRATION_MINUTES = 5;

    public InterServerTokenHolder(String token, LocalDateTime issuedAt) {
        this.token = token;
        this.issuedAt = issuedAt;
    }

    /**
     * 현재 시간이 토큰 발급 시간으로부터 5분을 초과했는지 확인합니다.
     * @param clock 현재 시간을 판단할 기준 (테스트 주입 가능)
     * @return 만료되었으면 true
     */
    public boolean isExpired(Clock clock) {
        return issuedAt.plusMinutes(EXPIRATION_MINUTES).isBefore(LocalDateTime.now(clock));
    }

    public String getToken() {
        return token;
    }
}
