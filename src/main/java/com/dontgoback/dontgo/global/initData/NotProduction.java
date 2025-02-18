package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev") // dev, test 환경에서만 사용
public class NotProduction {

    @Bean
    CommandLineRunner initPostData(FeedService feedService, UserService userService) {
        // 회원 가입
        User user1 = userService.createDummyUser("BackDummy1", "user1@email.com", RedBlueType.BLUE);
        User user2 = userService.createDummyUser("BackDummy2", "user2@email.com", RedBlueType.RED);

        return (args) -> {
            feedService.createDummyFeed(user1, "내용1");
            feedService.createDummyFeed(user1, "내용2");
            feedService.createDummyFeed(user1, "내용3");
            feedService.createDummyFeed(user2, "아집에가고싶다람쥐0");
            feedService.createDummyFeed(user1, "내용4");
            feedService.createDummyFeed(user1, "내용5");
            feedService.createDummyFeed(user1, "내용6");
            feedService.createDummyFeed(user1, "내용7");
            feedService.createDummyFeed(user2, "아집에가고싶다람쥐1");
            feedService.createDummyFeed(user2, "아집에가고싶다람쥐2");
            feedService.createDummyFeed(user1, "내용9");
            feedService.createDummyFeed(user1, "내용10");
            feedService.createDummyFeed(user2, "아집에가고싶다람쥐3");
            feedService.createDummyFeed(user2, "아집에가고싶다람쥐4");
            feedService.createDummyFeed(user2, "아집에가고싶다람쥐5");
            feedService.createDummyFeed(user1, "내용11");
        };
    }
}
