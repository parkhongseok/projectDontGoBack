package com.dontgoback.dontgo.global.security;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
// 기존의 시큐리티 필터를 상속받아서 재정의
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 Authorization 값을 가져온다
        String bearerToken = request.getHeader("Authorization");

        // 헤더 안에 토큰이 있다면
        if (bearerToken != null){
            String token = bearerToken.substring("Bearer ".length());
            // 유효성 검증
            if (jwtProvider.verify(token)){
                Map<String, Object> claims = jwtProvider.getClaims(token);
                long id = (int)claims.get("id");
                // 통과하면 id를 가져와서, 회원을 찾고
                User user = userService.findById(id).orElseThrow();
                // 성공하면, 회원에 권한 추가
                forceAuthentication(user);
            }
        }
        filterChain.doFilter(request, response);
    }

    // 강제 로그인 처리 메서드
    private void forceAuthentication(User user){
        // 이름이 중복되어 길게 선언, 근데 파라미터의 두번째 인자로 비밀번호가 와야하는데 일단 이메일 넣어둠
        org.springframework.security.core.userdetails.User securityUser
                = new org.springframework.security.core.userdetails.User(user.getUserName(), user.getEmail(), user.getAuthorities());

        // 스프링 시큐리티 객체에 저장할 authentication 객체 생성
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        securityUser,
                        null,
                        user.getAuthorities()
                );

        // 스프링 시큐리티 내에 커스텀한 authentication 객체를 저장할 context 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // context에 authentication 객체 저장
        context.setAuthentication(authentication);
        // 스프링 시큐리티에 context 등록
        SecurityContextHolder.setContext(context);
    }
}
