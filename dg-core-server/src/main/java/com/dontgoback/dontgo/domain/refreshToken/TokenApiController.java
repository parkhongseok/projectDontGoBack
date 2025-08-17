package com.dontgoback.dontgo.domain.refreshToken;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.dontgoback.dontgo.global.util.GlobalValues.REFRESH_TOKEN_NAME;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;
    private final TokenCookieService tokenCookieService;

    // Post 요청이 오면, 리프레시 토큰을 기반으로 새로운 액세스 토큰을 생성해서 반환해줌.
    @PostMapping("/api/token")
    public ResponseEntity<Void> createNewAccessToken
            (@CookieValue(name = REFRESH_TOKEN_NAME, required = false) String refreshToken,
             HttpServletResponse response){
        // 토큰이 null 이라면?
        if (refreshToken == null) {
            // 401 Unauthorized 응답 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        // 쿠키 서비스를 사용하여 응답에 쿠키 추가
        tokenCookieService.addAccessTokenCookie(response, newAccessToken);

        // 바디 없이 성공 응답(200 OK) 반환
        return ResponseEntity.ok().build();
    }

    /**
     * 방문자 로그인 API (TokenCookieService를 사용하도록 수정)
     */
    @PostMapping("/api/guest")
    public ResponseEntity<Void> guestLogin(HttpServletResponse response) {
        tokenService.processGuestLogin(response); // 서비스 로직은 response만 넘겨주도록 단순화 가능
        return ResponseEntity.ok().build();
    }
}