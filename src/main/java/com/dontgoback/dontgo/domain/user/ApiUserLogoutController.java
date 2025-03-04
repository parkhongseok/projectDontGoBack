package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenService;
import com.dontgoback.dontgo.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

import static com.dontgoback.dontgo.global.util.GlobalValues.REFRESH_TOKEN_NAME;

@RestController
@RequiredArgsConstructor
public class ApiUserLogoutController {
    private final RefreshTokenService refreshTokenService;

    // 토큰이 있는 기기에서만 로그아웃
    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 refresh_token 쿠키 가져오기
        Optional<Cookie> optionalCookie = CookieUtil.findCookie(request.getCookies(), REFRESH_TOKEN_NAME);
        // 데이터베이스에서 해당 Refresh Token 찾아서 삭제
        optionalCookie.ifPresent(cookie -> refreshTokenService.deleteByToken(cookie.getValue()));
        // 쿠키 무효화 (만료된 쿠키 생성)
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_NAME);
        // SecurityContext 초기화 (인증 정보 제거)
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        // 응답 반환 (프론트에서 리디렉트 수행)
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }
    // 필터 체인 상에서 logout은 비활성화함 하지만 ㄱㅊ
    //Spring Security는 SecurityContextHolder를 통해 현재 요청의 인증 정보를 관리함.
    //기본적으로 세 가지 SecurityContextHolderStrategy 방식이 존재함:
    //
    //MODE_THREADLOCAL (기본값) → 현재 쓰레드 내에서만 SecurityContext 유지
    //MODE_INHERITABLETHREADLOCAL → 자식 쓰레드까지 SecurityContext 공유
    //MODE_GLOBAL → 애플리케이션 전체에서 SecurityContext 공유
    //현재 Spring Security 설정에서 세션을 사용하지 않고 (STATELESS) 있기 때문에, SecurityContext는 요청이 끝나면 사라짐.
    //즉, 로그아웃 이후 새 요청이 들어오면 SecurityContext는 다시 생성되므로, 로그아웃 후에도 이전 인증 정보가 남아있지는 않음
}
