package com.dontgoback.dontgo.domain.refreshToken;

import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenCookieService tokenCookieService;
    private final UserRepository userRepository;
    private final AccountCreateService accountCreateService;

    private static final String GUEST_EMAIL = "guest@dontgoback.kro.kr";

    @Transactional
    public String createNewAccessToken(String refreshToken){
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.isTokenValid(refreshToken)){
            throw new IllegalArgumentException("Unexpect refreshToken token");
        }

        RefreshToken refreshTokenObj = refreshTokenService.findByRefreshToken(refreshToken);
        User user = refreshTokenObj.getUser();

        // 두시간짜리 액세스 토큰 발급
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }

    /**
     * 방문자 로그인을 처리하고 토큰을 쿠키에 설정
     */
    @Transactional
    public void processGuestLogin(HttpServletResponse response) {
        // 1 & 2. 방문자 계정을 조회하거나 없으면 생성
        User guestUser = findOrCreateGuestUser();

        // 3. 액세스 토큰과 리프레시 토큰을 발급
        String accessToken = tokenProvider.generateToken(guestUser, Duration.ofHours(2));
        String refreshToken = tokenProvider.generateToken(guestUser, Duration.ofDays(14));

        /* 4. 리프레시 토큰을 DB에 저장/갱신 */
        refreshTokenService.saveOrUpdateRefreshToken(guestUser, refreshToken);

        // 5. 두 토큰을 모두 쿠키에 추가
        tokenCookieService.addAccessTokenCookie(response, accessToken);
        tokenCookieService.addRefreshTokenCookie(response, refreshToken);
    }

    /**
     * 방문자 계정을 찾거나, 없으면 GUEST 역할로 새로 생성하는 헬퍼 메서드
     */
    private User findOrCreateGuestUser() {
        return userRepository.findByEmail(GUEST_EMAIL)
                .orElseGet(() -> accountCreateService.createGuestAccount(GUEST_EMAIL));
    }
}
