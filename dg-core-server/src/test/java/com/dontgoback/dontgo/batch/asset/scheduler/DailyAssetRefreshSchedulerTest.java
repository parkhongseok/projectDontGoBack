package com.dontgoback.dontgo.batch.asset.scheduler;

import com.dontgoback.dontgo.batch.asset.job.AssetRefreshJob;
import com.dontgoback.dontgo.batch.common.config.BatchProperties;
import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyAssetRefreshSchedulerTest {

    @Mock
    BatchProperties props;
    @Mock
    AssetRefreshJob job;

    DailyAssetRefreshScheduler scheduler;

    LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        scheduler = new DailyAssetRefreshScheduler(props, job);
    }

    @Test
    void run_should_skip_when_disabled() {
        when(props.getAssetRefresh()).thenReturn(assetProps(false));
        scheduler.run();
        verifyNoInteractions(job);
    }

    @Test
    void run_should_delegate_to_job_and_log() {
        when(props.getAssetRefresh()).thenReturn(assetProps(true));
        when(job.run(today)).thenReturn(BatchResult.builder()
                .total(10).success(9).failed(1).elapsedMs(1234).build());

        scheduler.run();

        verify(job, times(1)).run(today);
        // 로그 검증이 필요하면 OutputCaptureExtension 사용 (SpringBootTest에서)
    }

    @Test
    void run_should_catch_any_exception_from_job() {
        when(props.getAssetRefresh()).thenReturn(assetProps(true));
        when(job.run(today)).thenThrow(new RuntimeException("boom"));

        // 예외가 퍼지면 안됨(스케줄러 생명주기 보호)
        assertDoesNotThrow(() -> scheduler.run());
    }

    private BatchProperties.AssetRefresh assetProps(boolean enabled) {
        BatchProperties.AssetRefresh a = new BatchProperties.AssetRefresh();
        a.setEnabled(enabled);
        a.setCron("0 0 3 * * *");
        a.setTimezone("Asia/Seoul");
        return a;
    }
}
