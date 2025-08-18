package com.dontgoback.dontgo.batch.asset.scheduler;

import com.dontgoback.dontgo.batch.asset.job.AssetRefreshJob;
import com.dontgoback.dontgo.batch.common.config.BatchProperties;
import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;



/** scheduler 역할
 * 크론 스케줄에 맞춰 작업을 트리거만 한다.
 * 토글/락/예외로그 같은 운영 제어가 주 업무.
 * 여기서 하지 말 것: 비즈니스 로직, 트랜잭션, 대량 루프.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyAssetRefreshScheduler {
    private final BatchProperties props;
    private final AssetRefreshJob job;

    @Scheduled(cron = "#{@batchProperties.assetRefresh.cron}",
            zone = "#{@batchProperties.assetRefresh.timezone}")
    public void run() {
        if (!props.getAssetRefresh().isEnabled()) {
            log.info("[AssetRefresh] disabled, skip.");
            return;
        }
        long t0 = System.currentTimeMillis();
        try {
            BatchResult r = job.run();
            log.info("[AssetRefresh] done. total={}, success={}, failed={}, {}ms",
                    r.getTotal(), r.getSuccess(), r.getFailed(), System.currentTimeMillis()-t0);
        } catch (Exception e) {
            log.error("[AssetRefresh] failed: {}", e.getMessage(), e);
        }
    }
}
