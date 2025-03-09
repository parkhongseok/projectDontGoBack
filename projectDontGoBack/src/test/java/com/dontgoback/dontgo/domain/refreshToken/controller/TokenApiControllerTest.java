package com.dontgoback.dontgo.domain.refreshToken.controller;


import com.dontgoback.dontgo.config.jwt.JwtProperties;
import com.dontgoback.dontgo.domain.refreshToken.RefreshToken;
import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenRepository;
import com.dontgoback.dontgo.domain.refreshToken.dto.CreateAccessTokenRequest;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dontgoback.dontgo.config.jwt.JwtFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
/*
 * AutoConfigureMockMvc는 MockMvc를 생성하고, 자동으로 구성해줌?
 * 애플리케이션을 서버에 배포하지 않아도, 테스트용 MVC환경을 만들어서,
 * 가상의 요청, 전송, 응답을 테스트할 수 있음
 * => 컨트롤러를 테스트할 때 사용되는 클래스*/
public class TokenApiControllerTest {
    protected MockMvc mockMvc;
    /*
    외부에서 직접 수정할 필요가 없으므로 private이 적절
    (Spring의 DI 컨테이너에서 자동으로 주입되므로 외부 접근이 필요하지 않음
    */
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    // 아래 둘 용도
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper objectMapper;

    /* 왜 필요한가?

    @AutoConfigureMockMvc를 사용하면 자동으로 MockMvc가 주입되지만,
    위처럼 수동으로 초기화하면 추가적인 설정을 할 수 있음.
    Spring MVC의 전체 컨텍스트를 활용하기 때문에 필터, 인터셉터 등도 포함된 상태에서 테스트 가능*/
    @BeforeEach
    public void MockMvcSetUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
//        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken(): 리프레시 토큰으로 새로운 액세스 토큰 발급")
    @Test
    void createNewAccessToken() throws Exception {
        // Given
        // 테스트 유저 생성 > 리프레시 토큰 생성 / DB 저장
        // 토큰 생성 API의 요청 본문에 리프레시 토큰을 포함하여 요청 객체 생성

        // 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        final String url = "/api/token";

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);
        // 리프레시 토큰 생성
        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        // request DTO에 토큰 포함시키기
        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        // Http요청 본문에 DTO 포함시키기 (예외 포함)
        final String requestBody = objectMapper.writeValueAsString(request);

        // When
        // 토큰 추가 API에 요청 전송.
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // Then
        // 응답 코드가 201인지 확인하고 응답으로 온 액세스 토큰이 비었는지 확인
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}