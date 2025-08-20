package com.dontgoback.dontgo.config.oauth;


import com.dontgoback.dontgo.global.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

/*
 - 개념
         기본적으로 spring security는 HttpSessionOAuth2AuthorizationRequestRepository의 세션 기반인데,
         해당 클레스를 통해 쿠키 기반의 무상태 통신을 할 수 있도록  인증 요청을 처리, by 쿠키 유틸
 - 코드 흐름
        사용자가 OAuth2 로그인을 시도하면, OAuth2 인증 요청(OAuth2AuthorizationRequest)이 발생
        saveAuthorizationRequest() → 쿠키에 저장
        이후 OAuth2 제공자(Google, Naver 등)로 리디렉트되었다가, 인증이 완료된 후 다시 돌아옴
        loadAuthorizationRequest() → 쿠키에서 요청 정보 가져오기
        removeAuthorizationRequest() → 사용 완료 후 삭제
*/
public class OAuth2AuthorizationRequestBasedOnCookieRepository
        implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    // AuthorizationRequestRepository : 권한 인증 흐름에서 클라이언트 요청을 유지하는 데 필요한 인터페이스
    // OAuth2 인증 요청 정보를 쿠키에 저장하고 불러오는 역할을 하는 클래스
    public final static String OAUTH2_AUTHENTICATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS = 18000;

    @Value("${cookie.secure}")
    private boolean secureCookie;

    // OAuth2 인증 요청을 읽어오면서 제거하는 역할
    // 하지만 쿠키를 직접 삭제하지는 않음
    // 인증 과정에서 요청 정보를 불러올 때 사용됨
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response){
        return this.loadAuthorizationRequest(request); // 인터페이스에 명세된 메서드 호출
    }

    // 인증 요청에서 쿠키 꺼내고, 문자열 쿠키를 객체로 만들어서 반환
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHENTICATION_REQUEST_COOKIE_NAME);
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
    }

    // 서비스 서버 -> 사용자 : 사용자에게 보낼 응답 메시지에 쿠키 추가하기 (이를 통해 클라이언트에서 다시 쿠키를 가져올 수 있도록 함)
    @Override
    public void saveAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest,
            HttpServletRequest request,
            HttpServletResponse response){
        if (authorizationRequest == null){
            removeAuthorizationRequest(request, response);
            return;
        }
        CookieUtil.addCookie(response,
                OAUTH2_AUTHENTICATION_REQUEST_COOKIE_NAME,
                CookieUtil.serialize(authorizationRequest),
                COOKIE_EXPIRE_SECONDS, secureCookie, "/");
    }

    // OAuth2 인증이 완료되면 해당 요청 정보를 더 이상 유지할 필요가 없으므로 쿠키를 삭제 (더이상 쿠키가 필요없으니까)
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response){
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHENTICATION_REQUEST_COOKIE_NAME, secureCookie, "/");
    }
}
