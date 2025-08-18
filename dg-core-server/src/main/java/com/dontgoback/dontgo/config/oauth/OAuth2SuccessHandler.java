package com.dontgoback.dontgo.config.oauth;


import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenService;
import com.dontgoback.dontgo.domain.refreshToken.TokenCookieService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;


// 필터 맥락에서, 사용자에게 로그인 페이지를 전달하고, 사용자는 로그인을 수행, 그리고 로그인 성공 시 실행되는 핸들러
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenCookieService tokenCookieService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);

    @Value("${app.FRONTEND_URL}")
    public String FRONTEND_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        if (!user.isEnabled()) {
            // 토큰 제거, 리다이렉션 등 비정상 처리
            response.sendRedirect(FRONTEND_URL + "/login?status=SUSPENDED");
            return;
        }


        // 리프레시 토큰 생성 -> 저장 -> 쿠키에 저장
        /* TokenProvider를 사용하여 리프레시 토큰을 만들고, saveRefreshToken() 메서드를 호출하여 해당 토큰을 DB에 유저 id와 함께 저장
        이후 클라이언트에서 액세스토큰이 만료되면, 재발급을 요청하도록 addRefreshTokenToCookie()를 호출해서 쿠키에 리프레시 토큰을 저장 */
        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

        refreshTokenService.saveOrUpdateRefreshToken(user, refreshToken);

        tokenCookieService.addRefreshTokenCookie(response, refreshToken);

        /* 액세스 토큰을 클라이언트한테 전달하기
                HOW ? 리다이렉트 경로에 액세스 토큰 집어넣기
                리다이렉트 경로는 어딨음? 쿠키에 */
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        tokenCookieService.addAccessTokenCookie(response, accessToken);

        /* 인증 관련 설정값, 쿠키 제거 (SimpleUrlAuthenticationSuccessHandler 의 메서드 말고, 아래에서 구현)
           인증 프로세스를 진행하면서, 세션과 쿠키에 임시로 저장해둔 인증 관련 데이터를 제거 */
        clearAuthenticationAttributes(request, response);

        /*리다이렉트 (SimpleUrlAuthenticationSuccessHandler -> <<AbstractAuthenticationTargetUrlRequestHandler>> 의 메서드)
        위에서 만든 URL로 리다이렉트 */
        getRedirectStrategy().sendRedirect(request, response, getTargetUrl());
    }

    // 인증 관런 설정값, 쿠키 제거
    // 원래 이 메서드는 세션 기반의 SimpleUrlAuthenticationSuccessHandler에서의 메서드인데, 여기서 오버로딩
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        // 이제 토큰 필요 없으니까 제거
    }

    // 향후 파라미터가 추가될 경우를 대비
    private String getTargetUrl() {
        return UriComponentsBuilder.fromUriString(FRONTEND_URL)
//                .queryParam(ACCESS_TOKEN_NAME, token)
                .build()
                .toUriString();
    }

}
