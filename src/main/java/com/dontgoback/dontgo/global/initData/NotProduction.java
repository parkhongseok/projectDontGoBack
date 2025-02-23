package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.comment.CommentService;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@Profile("dev") // dev, test 환경에서만 사용
public class NotProduction {

    @Bean
    CommandLineRunner initPostData(FeedService feedService, UserService userService, CommentService commentService) {
        // 회원 가입
        User user1 = userService.createDummyUser("BackDummy1", "user1@email.com", RedBlueType.BLUE);
        User user2 = userService.createDummyUser("BackDummy2", "user2@email.com", RedBlueType.RED);

        List<Integer> nums = IntStream.range(1, 50)
                                            .boxed()
                                            .toList();
        List<Feed> feeds = new ArrayList<>();

        for (int i : nums){
            User user = (i % 2 == 0) ? user1 : user2;
            feeds.add(feedService.createDummyFeed(user, "Server : 자동 생성 피드%d".formatted(i)));
        }


        return (args) -> {
            for (Feed feed : feeds) {
                // 각 피드에 댓글을 추가 (예시: 1번 피드에 댓글 3개 추가)
                commentService.createDummyComment(feed, user1, "Server : 자동 생 성 댓글 for Feed ID: " + feed.getId());
                commentService.createDummyComment(feed, user2, "Server : 자동 생성 댓글 for Feed ID: " + feed.getId());
            }
        };
    }
}
