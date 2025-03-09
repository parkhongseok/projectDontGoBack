//package com.dontgoback.dontgo.domain.feed.feedController;
//
//import com.dontgoback.dontgo.domain.feed.Feed;
//import com.dontgoback.dontgo.domain.feed.FeedRepository;
//import com.dontgoback.dontgo.domain.feed.dto.CreateFeedRequest;
//import com.dontgoback.dontgo.domain.feed.dto.FeedResponse;
//import com.dontgoback.dontgo.domain.feed.dto.FeedsResponse;
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.domain.user.UserRepository;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.security.Principal;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class FeedControllerTest {
//    @Autowired
//    protected MockMvc mockMvc;
//
//    @Autowired
//    protected ObjectMapper objectMapper;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    FeedRepository feedRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    User user;
//
//    @BeforeEach
//    public void mockSetUp(){
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//                .build();
//        feedRepository.deleteAll();
//    }
//
//    @BeforeEach
//    void setSecurityContext() {
//        userRepository.deleteAll();
//        user = userRepository.save(User.builder()
//                .email("test@email.com")
//                .userAsset("Test")
//                .userType(RedBlueType.BLUE)
//                .build());
//
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        securityContext.setAuthentication(
//                    new UsernamePasswordAuthenticationToken(
//                                                user,
//                                                user.getPassword(),
//                                                user.getAuthorities()
//                    )
//        );
//    }
//
//
//    @DisplayName("addFeed : 피드 생성에 성공한다.")
//    @Test
//    public void addFeed() throws Exception {
//        // given
//        final String url = "/api/v1/feeds";
//        final String content = "test Content";
//        final CreateFeedRequest createFeedRequest = new CreateFeedRequest(content);
//
//        final String requestBody = objectMapper.writeValueAsString(createFeedRequest);
//
////        Principal principal = Mockito.mock(Principal.class);
////        Mockito.when(principal.getName()).thenReturn("username");
//        // SecurityContext에서 Principal 가져오기
//        Principal principal = SecurityContextHolder.getContext().getAuthentication();
//
//        // when
//        ResultActions result = mockMvc.perform(post(url)
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .principal(principal)
//                .content(requestBody));
//
//        // then
//        // 명시적으로 반환 타입을 컨트롤러에서 설정할 필요가 있을듯?
//        // 현재 ResData 대신 그냥 스프링 제공 ResponseEntity 사용할 지 고민 -> 어차피 반환 코드도 좀 애매하고
//        result.andExpect(status().isCreated());
//
//
//        FeedsResponse feedsResponse = new FeedsResponse(
//                feedRepository.findFeedsResponse(1L, 10, 2L)
//        );
//
//        assertThat(feedsResponse.getFeeds().size()).isEqualTo(1);
//        assertThat(feedsResponse.getFeeds().get(0).getContent()).isEqualTo(content);
//    }
//
//
//
//}
