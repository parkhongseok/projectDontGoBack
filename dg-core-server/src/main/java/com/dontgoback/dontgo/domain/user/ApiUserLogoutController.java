package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenService;
import com.dontgoback.dontgo.domain.refreshToken.TokenCookieService;
import com.dontgoback.dontgo.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static com.dontgoback.dontgo.global.util.GlobalValues.ACCESS_TOKEN_NAME;
import static com.dontgoback.dontgo.global.util.GlobalValues.REFRESH_TOKEN_NAME;

@RestController
@RequiredArgsConstructor
public class ApiUserLogoutController {
    private final RefreshTokenService refreshTokenService;
    private final TokenCookieService tokenCookieService;

    // 토큰이 있는 기기에서만 로그아웃
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal User user,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {

        // 요청에서 refresh_token 쿠키 가져오고, => 쿠키의 Path 떄문에 불가능
        // 데이터베이스에서 해당 Refresh Token 찾아서 삭제
        refreshTokenService.deleteByUserId(user.getId());

        // 쿠키 무효화 (만료된 쿠키 생성)
        tokenCookieService.clearTokenCookies(request, response);

        // SecurityContext 초기화 (인증 정보 제거)
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        // 응답 반환 (프론트에서 리디렉트 수행)
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }
}
