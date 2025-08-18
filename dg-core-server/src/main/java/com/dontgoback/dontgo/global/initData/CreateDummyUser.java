package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile({"dev", "!test"})
public class CreateDummyUser {
    private final int NUMBER_OF_USERS = 3;
    private final long DEFAULT_USER_ASSET = 10000000;
    private final AccountCreateService accountCreateService;
    private final FeedService feedService;

    @Bean
    CommandLineRunner createNewUsers(){
       return args -> {
           for (int i = 0; i < NUMBER_OF_USERS; i++){
               String email = "apple" + i;
               User user = accountCreateService.createDefaultAccount(email);
               Feed feed = Feed.builder()
                               .user(user)
                               .content( "%d번 피드입니다.".formatted(i+1))
                               .feedType(RedBlueType.BLUE)
                               .author(user.getUserAsset())
                               .build();
               feedService.saveFeed(feed);
           }
       };
    }
}