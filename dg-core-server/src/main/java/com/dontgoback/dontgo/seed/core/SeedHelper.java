
package com.dontgoback.dontgo.seed.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedHelper {

    private final OneTimeSeedLockService lockService;

    /** 락 → 실행 → 로깅 템플릿. 재사용 가능. */
    public void runOnce(String lockKey, Runnable task) {
        if (!lockService.acquire(lockKey)) {
            log.warn("[SEED] lock={} exists. skip.", lockKey);
            return;
        }
        long t0 = System.currentTimeMillis();
        log.info("[SEED] start lock={}", lockKey);
        try {
            task.run();
        } finally {
            log.info("[SEED] done lock={} took={}ms", lockKey, System.currentTimeMillis() - t0);
        }
    }
}
