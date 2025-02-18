package com.dontgoback.dontgo.config.webMvc;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.net.http.WebSocket;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000") //"https://cdpn.io" 도 추가 하는 경우
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}