package com.dontgoback.dontgo.config.jwt;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import io.jsonwebtoken.Jwts;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

@SpringBootTest
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given
        // 테스트 유저 상정
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        // when
        // 토큰 제공자가 유저의 정보를 받아서, 토큰을 생성해줌
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then
        // 테스트 유저의 정보로 발급해준 토큰을 다시 복호화하여, 복호화했을 때 동일한 결과인지 검사
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        AssertionsForClassTypes.assertThat(userId).isEqualTo(testUser.getId());
        // 추가로 동일한 아이디로 호출한 두 번의 유저가 역시 동일한지 (영속성 컨텍스트에서 관리된다면 동일할 것, by 2차 캐시)
    }

    // 만료 시간에 대한 TEST
    @DisplayName("validToken(): 만료된 토큰인 경우에 유효성 검증에 실패한다.")
    @Test
    void validToken_invalidToken() {
        // given
        // 토큰 생성
        String token = JwtFactory.builder()
                // 만료시간 자체를 현재 시간 기준으로 이전으로 설정하여, 이미 만료된 토큰으로 애초에 생성함
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // when
        // 토큰 제공자의 유효성 검사 메서드를 호출하여 그 결과를 반환받음
        boolean result = tokenProvider.validToken(token);

        // then
        // 결과 확인
        AssertionsForClassTypes.assertThat(result).isFalse();
    }

    // 유효한 토큰
    @DisplayName("validToken(): 유효한 토큰인 경우에 유효성 검증에 성공한다.")
    @Test
    void validToken_validToken() {
        // given
        // 다른 인자 없이 생성하는 경우, JwtFactory 에서 설정한 14일 뒤 기간으로 만료 기간 자동 설정
        String token = JwtFactory.withDefaultValues()
                .createToken(jwtProperties);

        // when
        boolean result = tokenProvider.validToken(token);


        // then
        AssertionsForClassTypes.assertThat(result).isTrue();
    }


    // 인증 정보를 담은 객체 테스트
    @DisplayName("getAuthentication(): 토큰 기반으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        // when
        // Authentication 인터페이스 타입의 인증정보 객체를 받아옴
        Authentication authentication = tokenProvider.getAuthentication(token);

        // then
        /* authentication 에서 token으로 생성한 authentication는 UsernamePasswordAuthenticationToken의 인스턴스를 반환하며,
        이는 Authentication 인터페이스의 구현체인데, 기본적으로 Principal이라는 객체와, token 객체를 기반으로 인증정보를 생성함
        파라미터로 Principal 자리에 User(security 제공 클래스)타입의 객체 입력
        User 객체에는 실제 유저의 정보가 아닌, 토큰의 클레임 중, 이메일 정보에 해당하는 Subject와, 임의의 비밀번호 "", 임시 인증정보 객체를 파라미터로 받음
        User 객체는 UserDetails의 구현체이며, 여기엔 유저 정보와 계정 상태 정보 등이 포함. => 따라서 형 변환 가능
        따라서 getName가능 => 따라서 해당 반환 값은 유저 객체 생성에 사용도니 이메일 정보임          */
        AssertionsForClassTypes.assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        // when
        Long userIdByToken = tokenProvider.getUserId(token);

        // then
        AssertionsForClassTypes.assertThat(userIdByToken).isEqualTo(userId);
    }
}