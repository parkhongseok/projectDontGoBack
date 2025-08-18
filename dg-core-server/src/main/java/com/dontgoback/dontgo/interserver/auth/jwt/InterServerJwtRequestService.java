package com.dontgoback.dontgo.interserver.auth.jwt;

import com.dontgoback.dontgo.interserver.auth.AuthServerProperties;
import com.dontgoback.dontgo.interserver.auth.client.InterServerClientProperties;
import com.dontgoback.dontgo.interserver.auth.jwt.dto.InterServerJwtRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
// Lombock 이 생성한 생성자에는 Qualifier 미반영 따라서 명시적으로 생성자 주입 필요
//@RequiredArgsConstructor
@Component
public class InterServerJwtRequestService {

    // @Qualifier("authRestTemplate")
    private final RestTemplate restTemplate;
    private final AuthServerProperties authServerProps;
    private final InterServerClientProperties clientProps;

    // 생성자 주입
    public InterServerJwtRequestService(
            @Qualifier("authRestTemplate") RestTemplate restTemplate,
            AuthServerProperties authServerProps,
            InterServerClientProperties clientProps
    ) {
        this.restTemplate = restTemplate;
        this.authServerProps = authServerProps;
        this.clientProps = clientProps;
    }

    public String requestJwt() {
        String url = authServerProps.getHost() + "/msa/auth/api/token";
        log.info("인증 서버에 JWT 발급 요청 시작 → URL: {}", url);

        HttpEntity<InterServerJwtRequest> requestEntity = buildRequestEntity();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("JWT 발급 성공");
                return response.getBody();
            } else {
                log.warn("JWT 발급 실패 - 상태 코드: {}", response.getStatusCode());
                return null;
            }

        } catch (Exception e) {
            log.error("인증 서버 JWT 요청 중 예외 발생", e);
            return null;
        }
    }

    /**
     * 인증 서버 JWT 발급 요청용 RequestEntity 생성
     */
    private HttpEntity<InterServerJwtRequest> buildRequestEntity() {
        InterServerJwtRequest dto = new InterServerJwtRequest(
                clientProps.getId(),
                clientProps.getSecret()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(dto, headers);
    }
}
