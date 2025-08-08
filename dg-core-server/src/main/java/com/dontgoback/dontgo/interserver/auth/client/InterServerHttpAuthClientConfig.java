package com.dontgoback.dontgo.interserver.auth.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class InterServerHttpAuthClientConfig {
    private final int TIME_OUT_SECOND = 3000;

    /**
     * 인증 서버로 JWT를 요청하기 위한 RestTemplate 빈 설정
     *
     * - 인증 서버 장애 시, 전체 요청 쓰레드 무기한 대기 방지
     * - 적절한 타임아웃 설정으로 서비스 지연 최소화
     */
    @Bean(name = "authRestTemplate")
    public RestTemplate authRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIME_OUT_SECOND);
        factory.setReadTimeout(TIME_OUT_SECOND);
        return new RestTemplate(factory);
    }
}
