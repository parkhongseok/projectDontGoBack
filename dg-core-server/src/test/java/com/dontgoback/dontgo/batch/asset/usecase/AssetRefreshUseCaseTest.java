package com.dontgoback.dontgo.batch.asset.usecase;

import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import com.dontgoback.dontgo.interserver.extension.asset.InterServerAssetUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

//Service가 BatchResult를 반환하도록 바꿨으니 UseCase는 패스스루만 확인
@ExtendWith(MockitoExtension.class)
public class AssetRefreshUseCaseTest {

    @Mock
    InterServerAssetUpdateService updateService;

    AssetRefreshUseCase useCase;
    LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        useCase = new AssetRefreshUseCase(updateService);
    }

    @Test
    void refreshAllActiveUsers_should_return_service_result() {
        BatchResult expected = BatchResult.builder()
                .total(3).success(2).failed(1).elapsedMs(100).build();
        when(updateService.updateAllActiveUsersAsset(today)).thenReturn(expected);

        BatchResult actual = useCase.refreshAllActiveUsers(today);

        assertThat(actual).isEqualTo(expected);
        verify(updateService, times(1)).updateAllActiveUsersAsset(today);
    }
}
