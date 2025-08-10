package com.dontgoback.dontgo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.mockito.Mockito.when;

@Component
@RequiredArgsConstructor
public class ClockHelper {
    private final Clock clock; // TestClockConfig에서 생성된 Mock Clock Bean이 주입됩니다.

    public void setClock(LocalDate date) {
        Clock fixedClock = Clock.fixed(date.atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }
}