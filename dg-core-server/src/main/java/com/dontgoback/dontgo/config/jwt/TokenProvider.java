package com.dontgoback.dontgo.config.jwt;


import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.TokenPurpose;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private Key key;
    private JwtParser parser;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecretKey());
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();

        log.warn("[JWT] provider={} alg=HS256 issuer={} keyHash={}",
            System.identityHashCode(this),
            jwtProperties.getIssuer(),
            java.util.Arrays.hashCode(keyBytes)); // 프로파일/환경 불일치 탐지
    }

    // 일반 Access, Refresh 토큰 발급용
    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }
    // 메일 인증용, 탈퇴용 등 purpose가 있는 토큰
    public String generatePurposeToken(User user, Duration expiredAt, TokenPurpose purpose) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user, purpose);
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
                .claim("role", user.getRole().name())
                // 서명 : 비밀값 + 해시값을 HS256 방식으로 암호화
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String makeToken(Date expiry, User user, TokenPurpose purpose) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .claim("purpose", purpose)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 유효성 검증
    public boolean isTokenValid(String token) {

        try {
            // null 체크 추가: null이 들어올 경우를 대비한 방어 코드
            if (token == null) {
                log.warn("Token is null");
                return false;
            }
            Jws<Claims> jws = parser.parseClaimsJws(token);
            var h = jws.getHeader(); var c = jws.getBody();
//            log.warn("[JWT] valid alg={} iss={} sub={} exp={} now={}",
//                    h.getAlgorithm(), c.getIssuer(), c.getSubject(), c.getExpiration(), new Date());
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // 이 예외는 토큰이 비어있거나(empty) 다른 라이브러리 내부 문제일 때 발생할 수 있습니다.
            log.warn("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
        } catch (Exception e){
            log.warn("[JWT] invalid: {}", e.toString());
            return false;
        }
        return false;
    }

    public boolean isPurposeTokenValid(String token, TokenPurpose expectedPurpose) {
        try {
            Claims claims = getClaims(token);
            String actualPurpose = claims.get("purpose", String.class);
            return expectedPurpose.toString().equals(actualPurpose);
        } catch (Exception e) {
            log.warn("유효하지 않은 PurposeToken: {}", e.getMessage());
            return false;
        }
    }


    // 인증 정보를 가져오는 메서드
    // Authentication 인터페이스 타입의 객체 반환
    // 유저가 로그인 요청 -> 헤더에서 토큰 get -> [인증] -> 이 함수 파라미터로 전달 -> principal 유저등을 필드로 갖는 객체 반환
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class))
        );

        User user = User.builder()
                .id(claims.get("id", Long.class))
                .role(UserRole.valueOf(claims.get("role", String.class))) // 없애야할지
                .email(claims.getSubject())
                .build();


        // UsernamePasswordAuthenticationToken은 두 객체( principal, credentials ) 와 다른 List를 파라미터로 받음.
        // getPrincipal()메서드는 입력받은 유저 정보를 반환,
        // getCredentials() 메서드는 토큰 반환,
        return new UsernamePasswordAuthenticationToken(
                //인증 유저, 등을 필드로 갖는 객체 (시큐리티 기본 재공 user 객체 대신 userdetials를 구현한 나의 user 엔티티를 직접 사용
//                new org.springframework.security.core.userdetails.User(
//                        claims.getSubject(), "", authorities),
                user,
                token,
                authorities
        );
    }

    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    private Claims getClaims(String token) {
        return parser.parseClaimsJws(token)
                .getBody();
    }
}