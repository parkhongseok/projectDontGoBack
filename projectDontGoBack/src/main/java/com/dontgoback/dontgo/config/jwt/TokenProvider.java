package com.dontgoback.dontgo.config.jwt;


import com.dontgoback.dontgo.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ
                .setIssuer(jwtProperties.getIssuer()) // 패이로드 iss(이메일)
                .setIssuedAt(now)                     // 패이로드 iat : 현재시간
                .setExpiration(expiry)                // 패이로드 exp : expiry 멤버 변수
                .setSubject(user.getEmail())          // 패이로드 sub : 유저 이메일
                .claim("id", user.getId())      // 클레임 id : 유저 ID
                // 서명 : 비밀값 + 해시값을 HS256 방식으로 암호화
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    // JWT 유효성 검증
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey()) // 비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) { // 복호화 시 에러 나면 유효하지 않은 토큰
            return false;
        }
    }

    // 인증 정보를 가져오는 메서드
    // Authentication 인터페이스 타입의 객체 반환
    // 유저가 로그인 요청 -> 헤더에서 토큰 get -> [인증] -> 이 함수 파라미터로 전달 -> principal 유저등을 필드로 갖는 객체 반환
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        // UsernamePasswordAuthenticationToken은 두 객체( principal, credentials ) 와 다른 List를 파라미터로 받음.
        // getPrincipal()메서드는 입력받은 유저 정보를 반환,
        // getCredentials() 메서드는 토큰 반환,
        return new UsernamePasswordAuthenticationToken( //인증 유저, 등을 필드로 갖는 객체
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(), "", authorities),
                token,
                authorities
        );
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}