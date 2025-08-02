package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.user.AccountCreateService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class CreateDummyUser {
    private final int NUMBER_OF_USERS = 10;
    private final long DEFAULT_USER_ASSET = 10000000;
    private final AccountCreateService accountCreateService;

    @Bean
    CommandLineRunner createNewUsers(){
       return args -> {
           for (int i = 0; i < NUMBER_OF_USERS; i++){
               String email = "apple" + i;
               accountCreateService.createUserWithDefaultHistories(email);
           }
       };
    }
}
