//package com.dontgoback.dontgo.domain.feed;
//
//import com.dontgoback.dontgo.config.jwt.JwtFactory;
//import com.dontgoback.dontgo.config.jwt.JwtProperties;
//import com.dontgoback.dontgo.config.jwt.TokenProvider;
//import com.dontgoback.dontgo.domain.feed.dto.FeedResponse;
//import com.dontgoback.dontgo.domain.feed.dto.FeedsResponse;
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.domain.user.UserService;
//import com.dontgoback.dontgo.global.resData.ResData;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.ArrayList;
//
//import static java.lang.reflect.Array.get;
//
//import static org.mockito.BDDMockito.given;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@AutoConfigureMockMvc
//@SpringBootTest
//@Import(ApiV1FeedControllerTest.TestMockConfig.class)
//class ApiV1FeedControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private TokenProvider tokenProvider;
//
//    @Autowired
//    private FeedService feedService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private JwtProperties jwtProperties;
//
//    @TestConfiguration
//    static class TestMockConfig {
//
//        @Bean
//        public FeedService feedService() {
//            return Mockito.mock(FeedService.class);
//        }
//
//        @Bean
//        public UserService userService() {
//            return Mockito.mock(UserService.class);
//        }
//    }
//
//    @Test
//    @DisplayName("인증된 사용자로 메인 피드 조회 성공")
//    void getFeeds() throws Exception {
//        // given
//        Long userId = 42L;
//        String userEmail = "test@dontgoback.com";
//
//        User user = User.builder()
//                .id(userId)
//                .email(userEmail)
//                .build();
//
//        // JWT 토큰 생성
//        String token = JwtFactory.builder()
//                .id(userId)
//                .subject(userEmail)
//                .build()
//                .createToken(jwtProperties);
//
//        // feedService의 응답 mock 처리
//        FeedsResponse data = new FeedsResponse(new ArrayList<>());
//        ResData resData = ResData.of("S-200", "메인 피드 조회 성공", data);
//
//        given(feedService.getFeedsResponse(0L, 10, userId)).willReturn(data);
//
//        // when & then
//        mockMvc.perform(get("/api/v1/feeds")
//                        .header("Authorization", "Bearer " + token)
//                        .param("lastFeedId", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("S-200"))
//                .andExpect(jsonPath("$.message").value("메인 피드 조회 성공"));
//    }
//
//
//}
////
////    @Test
////    void getProfileFeeds() {
////    }
////
////    @Test
////    void getFeed() {
////    }
////
////    @Test
////    void createFeed() {
////    }
////
////    @Test
////    void update() {
////    }
////
////    @Test
////    void remove() {
////    }
