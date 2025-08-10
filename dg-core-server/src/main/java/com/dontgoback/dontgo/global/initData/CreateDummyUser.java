package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Profile({"dev", "!test"})
public class CreateDummyUser {
    private final int NUMBER_OF_USERS = 10;
    private final long DEFAULT_USER_ASSET = 10000000;
    private final AccountCreateService accountCreateService;
    private final FeedService feedService;

    @Bean
    CommandLineRunner createNewUsers(){
       return args -> {
           for (int i = 0; i < NUMBER_OF_USERS; i++){
               String email = "apple" + i;
               User user = accountCreateService.createUserWithDefaultHistories(email);
               feedService.createDummyFeed(user, "%d번 피드입니다.".formatted(i+1));
           }
       };
    }
}