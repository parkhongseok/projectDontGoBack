package com.dontgoback.dontgo.interserver.auth.jwt;

import com.dontgoback.dontgo.global.exception.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class InterServerApiExecutorTest {

    @InjectMocks
    private InterServerApiExecutor executor;

    @Mock
    private InterServerTokenManager tokenManager;

    // 샘플 응답 및 토큰
    private final String VALID_JWT = "valid.jwt.token";
    private final String RESPONSE_FROM_CLIENT = "응답 결과";

    /* ---------- 정상 흐름: 토큰을 받아 요청 실행 ---------- */
    @Test
    @DisplayName("JWT를 주입받아 요청을 정상 수행한다")
     void should_execute_request_with_jwt_token() {
        // given
        Mockito.when(tokenManager.getToken()).thenReturn(VALID_JWT);

        // 요청 함수 정의: 토큰을 받아 결과 반환
        Function<String, String> requestFunction = jwt -> {
            assertThat(jwt).isEqualTo(VALID_JWT); // 전달된 토큰이 올바른지 확인
            return RESPONSE_FROM_CLIENT;
        };

        // when
        String result = executor.executeWithToken(requestFunction);

        // then
        assertThat(result).isEqualTo(RESPONSE_FROM_CLIENT);
        Mockito.verify(tokenManager, Mockito.times(1)).getToken();
    }


    /* ---------- 첫 시도 실패 → 토큰 재발급 후 성공 ---------- */
    @Test
    @DisplayName("첫 요청 실패 시, 토큰을 재발급하고 재시도하여 성공한다")
    void should_retry_with_new_token_on_unauthorized() {
        // given
        String oldToken = "expired.jwt.token";
        String newToken = "new.jwt.token";

        Mockito.when(tokenManager.getToken()).thenReturn(oldToken);
        Mockito.when(tokenManager.forceRefresh()).thenReturn(newToken);

        // 요청 함수: 첫 요청은 실패, 두 번째 요청은 성공
        Function<String, String> requestFunction = jwt -> {
            if (jwt.equals(oldToken)) throw new UnauthorizedException("401 Unauthorized");
            if (jwt.equals(newToken)) return RESPONSE_FROM_CLIENT;
            throw new RuntimeException("예상하지 못한 토큰 값");
        };

        // when
        String result = executor.executeWithToken(requestFunction);

        // then
        assertThat(result).isEqualTo(RESPONSE_FROM_CLIENT);
        Mockito.verify(tokenManager, Mockito.times(1)).getToken();
        Mockito.verify(tokenManager, Mockito.times(1)).forceRefresh();
    }


    /* ---------- 예외 발생 시 전파 ---------- */
    @Test
    @DisplayName("기타 예외 발생 시 그대로 전파한다")
    void should_throw_other_exceptions_as_is() {
        // given
        Mockito.when(tokenManager.getToken()).thenReturn(VALID_JWT);

        Function<String, String> requestFunction = jwt -> {
            throw new IllegalStateException("예상치 못한 예외");
        };

        // when & then
        assertThatThrownBy(() -> executor.executeWithToken(requestFunction))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("예상치 못한 예외");

        Mockito.verify(tokenManager).getToken();
    }
}
