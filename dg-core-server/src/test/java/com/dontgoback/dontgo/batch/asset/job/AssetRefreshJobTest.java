package com.dontgoback.dontgo.batch.asset.job;

import com.dontgoback.dontgo.batch.asset.usecase.AssetRefreshUseCase;
import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AssetRefreshJobTest {
    @Mock
    AssetRefreshUseCase useCase;

    AssetRefreshJob job;

    @BeforeEach
    void setUp() {
        job = new AssetRefreshJob(useCase);
    }

    private LocalDate today = LocalDate.now();
    @Test
    void run_should_return_usecase_result() {
        BatchResult expected = BatchResult.builder()
                .total(5).success(5).failed(0).elapsedMs(321).build();
        when(useCase.refreshAllActiveUsers(today)).thenReturn(expected);

        BatchResult actual = job.run(today);

        assertThat(actual).isEqualTo(expected);
        verify(useCase, times(1)).refreshAllActiveUsers(today);
    }
}
