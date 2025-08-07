package com.dontgoback.dontgo.interserver.auth.jwt;

import com.dontgoback.dontgo.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * InterServer 간 API 요청 시, JWT 인증 흐름을 자동으로 처리해주는 래퍼 클래스입니다.
 *
 * - 토큰 만료 검사
 * - 자동 재발급
 * - 실패 시 재시도 (401 Unauthorized 대응)
 *
 * 사용자는 단순히 "JWT를 받아 요청하는 로직"만 정의하고,
 * 인증 흐름은 모두 이 래퍼가 관리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterServerApiExecutor {

    private final InterServerTokenManager tokenManager;

    /**
     * JWT 인증이 필요한 API 요청을 실행합니다.
     *
     * @param requestWithToken JWT를 받아 API 요청을 수행하는 함수형 인터페이스
     * @param <T> 결과 타입
     * @return API 요청 결과
     * @throws RuntimeException 인증 실패 또는 재시도 실패 등 예외 상황
     */
    public <T> T executeWithToken(Function<String, T> requestWithToken) {
        String token = tokenManager.getToken();

        try {
            return requestWithToken.apply(token);
        } catch (UnauthorizedException e) {
            log.warn("⚠️ JWT 인증 실패 - 토큰 재발급 시도 후 재요청");

            token = tokenManager.forceRefresh();

            try {
                return requestWithToken.apply(token);
            } catch (UnauthorizedException retryFail) {
                log.error("❌ 재발급 후에도 인증 실패. 요청 중단", retryFail);
                throw retryFail; // 상위 로직에서 핸들링할 수 있도록 전달
            }
        } catch (Exception e) {
            log.error("❌ API 요청 중 예상치 못한 예외 발생", e);
            throw e;
        }
    }
}
