package com.dontgoback.dontgo.domain.refreshToken;

import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken){
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("Unexpect token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);
        // 두시간짜리 액세스 토큰 발급
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
