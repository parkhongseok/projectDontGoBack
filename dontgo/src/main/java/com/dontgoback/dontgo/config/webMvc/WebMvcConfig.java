//package com.dontgoback.dontgo.config.webMvc;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//
////Spring Security 필터 체인 외부에서 요청을 처리
////Spring MVC에서 설정된 CORS는 보안 필터 체인을 거친 후에 적용되며, 일반적으로 컨트롤러의 메서드와 관련된 요청에만 영향
//@Configuration
//public class WebMvcConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/**")
//                .allowedOrigins("http://localhost:3000") //"https://cdpn.io" 도 추가 하는 경우
//                .allowedMethods("*")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//}