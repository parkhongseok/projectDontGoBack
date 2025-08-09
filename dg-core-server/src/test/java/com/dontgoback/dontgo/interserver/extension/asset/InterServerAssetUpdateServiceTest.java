package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.domain.accountStateHistory.AccountStateService;
import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistoryService;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.global.exception.UnauthorizedException;
import com.dontgoback.dontgo.interserver.auth.jwt.InterServerApiExecutor;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
@Rollback
public class InterServerAssetUpdateServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @MockBean
    private InterServerAssetRequestService assetClient;

    @MockBean
    private InterServerApiExecutor apiExecutor;

    @Autowired
    private InterServerAssetUpdateService assetUpdateService;

    @Autowired
    private AccountCreateService accountCreateService;

    /* ---------- 정상 응답 ---------- */
    @Test
    @DisplayName("자산 응답 수신 후 → DB에 이력 저장 및 유저 상태 반영")
    void should_persist_asset_history_and_update_user() {
        // given
        User user = accountCreateService.createUserWithDefaultHistories("test@email.com");

        long updatedAsset = 7777L;
        UpdateAssetResponse fakeResponse = new UpdateAssetResponse(user.getId(), updatedAsset);

        when(apiExecutor.executeWithToken(any()))
                .thenAnswer(invocation -> {
                    Function<String, UpdateAssetResponse> fn = invocation.getArgument(0);
                    return fn.apply("fake.jwt.token");
                });

        when(assetClient.updateAsset(Mockito.anyLong(), anyString(), any()))
                .thenReturn(fakeResponse);

        // when
        assetUpdateService.updateAllActiveUsersAsset();

        // then
        // 1. 자산 이력 저장 확인
        List<AssetHistory> histories = assetHistoryRepository.findAll();
        assertThat(histories).anyMatch(h -> h.getAmount() == updatedAsset && h.getUser().getId().equals(user.getId()));

        // 2. 유저의 현재 자산 변경 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCurrentAssetHistory().getAmount()).isEqualTo(updatedAsset);
    }


    /* ---------- 부분 실패 격리 ---------- */
    @Test
    @DisplayName("여러 유저 중 일부 실패해도 나머지는 저장된다")
    void partial_failures_are_isolated() {
        User u1 = accountCreateService.createUserWithDefaultHistories("u1@x.com");
        User u2 = accountCreateService.createUserWithDefaultHistories("u2@x.com");

        when(apiExecutor.executeWithToken(any())).thenAnswer(inv -> {
            Function<String, UpdateAssetResponse> fn = inv.getArgument(0);
            return fn.apply("jwt");
        });
        when(assetClient.updateAsset(eq(u1.getId()), anyString(), any()))
                .thenReturn(new UpdateAssetResponse(u1.getId(), 1111L));
        when(assetClient.updateAsset(eq(u2.getId()), anyString(), any()))
                .thenThrow(new RuntimeException("ext down"));

        assetUpdateService.updateAllActiveUsersAsset();

        // u1만 성공했는지
        assertThat(assetHistoryRepository.findAll())
                .anyMatch(h -> h.getUser().getId().equals(u1.getId()) && h.getAmount() == 1111L);
        // u2는 currentAssetHistory가 그대로인지
        User u2Reload = userRepository.findById(u2.getId()).orElseThrow();
        assertThat(u2Reload.getCurrentAssetHistory().getAmount())
                .isEqualTo(u2.getCurrentAssetHistory().getAmount());
    }
}
