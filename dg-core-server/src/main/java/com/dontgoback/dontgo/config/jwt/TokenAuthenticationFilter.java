package com.dontgoback.dontgo.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// 로그인 후 인가 처리
// 헤더 확인을 위해 추가한 커스텀 필터로, 세션, 폼 로그인 비활성화 이후 실행

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    // [인증] 헤더에서 토큰을 꺼내서 유효한 사용자인지 확인하고,
    // [인가] 그렇다면 시큐리티 필터 상에 인증된 사용자로 처리되도록 포함시킴
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = getAccessToken(request);

        // [인증] 가져온 토큰이 유효한지 확인 후, 유효한 경우 인증 정보 설정
        if (tokenProvider.isTokenValid(token)) {
            // getAuthentication메서드는 USER(security) 객체 반환하여 이를 인증된 사용자로 취급할 수 있도록 아래에 전달
            Authentication authentication = tokenProvider.getAuthentication(token);
            // 여기엔 유저 이름과 권한 목록 등의 정보가 포함 ->
            // [인가] 시큐리티가 해당 사용자를 인증된 사용자로 취급하도록 결정되는 부분
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 후속 필터 작업을 이어갈 수 있도록 메서드
        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 키가 'Authorization'인 필드의 값을 갖고와서,
    // 접두사인 Bearer를 제외한 값을 얻음
    // 만약 값이 null이거나 Beare로 시작하지 않으면 null 반환
    private String getAccessToken(HttpServletRequest request){
        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)){
            return authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
}
