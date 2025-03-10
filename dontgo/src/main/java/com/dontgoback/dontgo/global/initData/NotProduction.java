package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.comment.CommentService;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.feedLike.FeedLike;
import com.dontgoback.dontgo.domain.feedLike.FeedLikeService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
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
@Profile("dev")// dev, test 환경에서만 사용
public class NotProduction {

    @Bean
    CommandLineRunner initPostData(FeedService feedService, UserService userService, CommentService commentService, FeedLikeService feedLikeService) {
        // Dummy user 생성
        int userMax = 20;
        List<Integer> userRange = IntStream.range(0, userMax)
                .boxed()
                .toList();
        List<User> users = new ArrayList<>();
        for (int i : userRange){
            users.add(userService.createDummyUser(
                    "%d00만원".formatted(i+1),
                    "user%d@email.com".formatted(i),
                    (i % 2 == 0) ? RedBlueType.RED : RedBlueType.BLUE));
        }

        // Dummy 피드 생성
        int feedMax = 40;
        List<Integer> feedRange = IntStream.range(0, feedMax)
                                            .boxed()
                                            .toList();
        List<Feed> feeds = new ArrayList<>();

        for (int i : feedRange){
            User user = users.get(i % userMax);
            feeds.add(feedService.createDummyFeed(user, "%d번 피드가 자동으로 생성되고 말았어 크윽..".formatted(i+1)));
        }

        // Dummy feedLike 생성
        for (int i : feedRange){
            for (int j : userRange) {
                if (i%3==0){
                    Feed feed = feeds.get(i);
                    User user = users.get(j);
                    FeedLikeId feedLikeId = new FeedLikeId(user.getId(), feed.getId());
                    feedLikeService.saveFeedLike(new FeedLike(feedLikeId));
                }
            }
        }

        return (args) -> {
            // Dummy Comment 생성
            int commentMax = 10;
            for (int i : feedRange) {
                for (int j : userRange) {
                     if (i%2==0){
                        if (j == commentMax) break;
                        Feed feed = feeds.get(i);
                        User user = users.get((i+j) % userMax);
                        commentService.createDummyComment(feed, user,  "%d번 피드에 %d번 유저의 댓글이 자동으로 생성되고 말았어 크윽..".formatted(feed.getId(), user.getId()));
                    }
                }
            }
        };
    }
}
