package com.dontgoback.dontgo.interserver.extension;


import com.dontgoback.dontgo.util.ClockHelper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Clock;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Import(ClockHelper.class) // TestClockHelper를 Spring 컨텍스트에 포함시킵니다.
public class TestClockConfig {

    @Bean
    public Clock clock() {
        return mock(Clock.class);
    }

}
