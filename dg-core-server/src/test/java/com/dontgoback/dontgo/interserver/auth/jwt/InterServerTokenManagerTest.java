package com.dontgoback.dontgo.interserver.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("InterServerTokenManager 토큰 캐시 및 재발급 테스트")
@ExtendWith(value = MockitoExtension.class)
public class InterServerTokenManagerTest {
    @Mock
    private InterServerJwtRequestService jwtRequestService;

    private Clock fixedClock;
    private InterServerTokenManager tokenManager;

    private final String FIRST_TOKEN = "first.jwt.token";
    private final String SECOND_TOKEN = "second.jwt.token";

    @BeforeEach
    void setUp() {
        // 고정된 시계 (2025-08-07T12:00:00)
        fixedClock = Clock.fixed(
                LocalDateTime.of(2025, 8, 7, 12, 0)
                        .atZone(
                                ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault()
                        );
        tokenManager = new InterServerTokenManager(jwtRequestService, fixedClock);
    }




    /* ---------- 최초 토큰 발급 ---------- */
    @Test
    @DisplayName("최초 호출 시 토큰을 발급받는다")
    void should_issue_token_on_first_call() {
        // given
        Mockito.when(jwtRequestService.requestJwt()).thenReturn(FIRST_TOKEN);

        // when
        String token = tokenManager.getToken();

        // then
        assertThat(token).isEqualTo(FIRST_TOKEN);
        Mockito.verify(jwtRequestService, Mockito.times(1)).requestJwt();
    }


    /* ---------- 캐싱된 토큰 재사용 ---------- */
    @Test
    @DisplayName("캐시된 토큰은 재사용된다 (만료 전)")
    void should_use_cached_token_if_not_expired() {
        // given
        Mockito.when(jwtRequestService.requestJwt()).thenReturn(FIRST_TOKEN);

        // when
        String token1 = tokenManager.getToken(); // 발급
        String token2 = tokenManager.getToken(); // 재사용

        // then
        assertThat(token1).isEqualTo(token2);
        Mockito.verify(jwtRequestService, Mockito.times(1)).requestJwt();
    }


    /* ---------- 만료된 토큰 재발급 ---------- */
    @Test
    @DisplayName("토큰이 만료되면 새로 발급한다")
    void should_reissue_token_if_expired() {
        // given
        // 첫 번째 발급: 고정된 시간
        Mockito.when(jwtRequestService.requestJwt())
                .thenReturn(FIRST_TOKEN)
                .thenReturn(SECOND_TOKEN); // 두 번째 호출 시 두 번째 토큰 반환

        // 1. 첫 호출로 토큰 발급
        String token1 = tokenManager.getToken();

        // 2. 인위적으로 토큰 홀더의 발급 시각을 10분 전으로 설정 (만료 시뮬레이션)
        ReflectionTestUtils.setField(
                tokenManager,
                "holder",
                new InterServerTokenHolder(FIRST_TOKEN, LocalDateTime.now(fixedClock).minusMinutes(10))
        );

        // when
        // 3. 다시 호출하면 만료로 간주 → 재발급
        String token2 = tokenManager.getToken();

        // then
        assertThat(token1).isEqualTo(FIRST_TOKEN);
        assertThat(token2).isEqualTo(SECOND_TOKEN);
        Mockito.verify(jwtRequestService, Mockito.times(2)).requestJwt();
    }

    /* ---------- 강제 재발급 ---------- */
    @Test
    @DisplayName("forceRefresh 호출 시 토큰을 무조건 재발급한다")
    void should_force_reissue_token() {
        // given
        Mockito.when(jwtRequestService.requestJwt())
                .thenReturn(FIRST_TOKEN)
                .thenReturn(SECOND_TOKEN);

        // 1. 최초 발급
        String token1 = tokenManager.getToken();

        // 2. 강제 재발급
        String token2 = tokenManager.forceRefresh();

        // then
        assertThat(token1).isEqualTo(FIRST_TOKEN);
        assertThat(token2).isEqualTo(SECOND_TOKEN);
        Mockito.verify(jwtRequestService, Mockito.times(2)).requestJwt();
    }

    /* ---------- 발급 실패 시 예외 ---------- */
    @Test
    @DisplayName("발급 실패 시 예외를 던진다")
    void should_throw_exception_if_token_issue_failed() {
        // given
        Mockito.when(jwtRequestService.requestJwt()).thenReturn(null);

        // expect
        assertThatThrownBy(() -> tokenManager.getToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT 발급 실패");
    }

    /* ---------- 강제 재발급 실패 시 예외 ---------- */
    @Test
    @DisplayName("강제 재발급 실패 시 예외를 던진다")
    void should_throw_exception_if_force_refresh_failed() {
        // given
        Mockito.when(jwtRequestService.requestJwt()).thenReturn(null);

        // expect
        assertThatThrownBy(() -> tokenManager.forceRefresh())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT 재발급 실패");
    }
}
