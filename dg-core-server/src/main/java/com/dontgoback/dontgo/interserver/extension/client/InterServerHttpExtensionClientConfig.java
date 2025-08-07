package com.dontgoback.dontgo.interserver.extension.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class InterServerHttpExtensionClientConfig {
    private final int TIME_OUT_SECOND = 7000;

    @Bean(name = "extensionRestTemplate")
    public RestTemplate extensionRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(TIME_OUT_SECOND);
        factory.setReadTimeout(TIME_OUT_SECOND);
        return new RestTemplate(factory);
    }
}
