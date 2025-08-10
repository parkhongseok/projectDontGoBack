package com.dontgoback.dontgo.interserver.auth.jwt;

import com.dontgoback.dontgo.interserver.auth.AuthServerProperties;
import com.dontgoback.dontgo.interserver.auth.client.InterServerClientProperties;
import com.dontgoback.dontgo.util.Print;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class InterServerJwtRequestServiceTest {

    @Autowired
    private AuthServerProperties authServerProperties;

    @Autowired
    private InterServerJwtRequestService jwtRequestService;

    /* ---------- 정상 요청 ---------- */
    @Test
    @DisplayName("인증 서버 - JWT 발급 요청 테스트")
    void should_issue_jwt_successfully() {
        // when
        String jwt = jwtRequestService.requestJwt();

        // then
        assertThat(jwt).isNotNull();
        assertThat(jwt).contains(".");
        Print.log(jwt, "JWT 발급 성공");
    }

    /* ---------- 잘못된 클라이언트 정보 요청 ---------- */
    @Test
    @DisplayName("인증 서버 - 잘못된 정보로 JWT 발급 요청 테스트")
    void should_fail_when_client_info_invalid() {
        // given: 잘못된 ClientId/Secret 주입을 위한 직접 생성
        InterServerClientProperties invalidProps = new FakeClientProperties("wrong", "invalid");
        InterServerJwtRequestService invalidService = new InterServerJwtRequestService(
                new RestTemplate(), // timeout 미적용
                new FakeAuthServerProperties(authServerProperties.getHost()+"/token"),
                invalidProps
        );

        // when
        String jwt = invalidService.requestJwt();

        // then
        assertThat(jwt).isNull();
        System.out.println("잘못된 요청 처리 확인: null 반환됨");
    }

    public static class FakeClientProperties extends InterServerClientProperties {
        public FakeClientProperties(String id, String secret) {
            this.setId(id);
            this.setSecret(secret);
        }
    }

    public static class FakeAuthServerProperties extends AuthServerProperties {
        public FakeAuthServerProperties(String host) {
            this.setHost(host);
        }
    }
}
