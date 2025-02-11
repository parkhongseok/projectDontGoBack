package com.dontgoback.dontgo.domain.feed;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest         // 테스트옹 애플리케이션 컨텍스트 생성, @SpringBootApplication 이 붙은 클래스를 찾고,
                        // 해당 클래스에 포함된 빈을 찾아서, 테스트용 애플리케이션 컨텍스트라는 걸 구성
@AutoConfigureMockMvc   // MockMvc 생성 및 자동 구성, 서버에 배포하지 않아도 자동으로 요청 전송, 응답 기능 제공(컨트롤러 테스트 용도)
class ApiV1FeedControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach             // 테스트 실행 전 실행되는 메서드
    public void setMockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }
    @AfterEach
    public void cleanUp(){
        userRepository.deleteAll();
    }


    @DisplayName("getAllUsers : 아티클 조회에 성공!")
    @Test
    public void getAllUsers() throws Exception {
         // given
        final String url = "/v1";
        User user = User
                .builder()
                .userType(RedBlueType.BLUE)
                .email("email")
                .username("name")
                .build();
        User savedUser = userRepository.save(user);

        // when
        final ResultActions result = mockMvc.perform(get(url).
                accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$[0].id").value(savedUser.getId()))
                .andExpect()


    }
}