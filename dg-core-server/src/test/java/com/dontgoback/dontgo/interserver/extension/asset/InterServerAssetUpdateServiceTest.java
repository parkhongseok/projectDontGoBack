package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.interserver.auth.jwt.InterServerApiExecutor;
import com.dontgoback.dontgo.interserver.extension.asset.dto.FakeUpdateAssetResponse;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetResponse;
import com.dontgoback.dontgo.util.ClockHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
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

    @MockBean
    private Clock clock;

    @Autowired
    private ClockHelper clockHelper;

    @Autowired
    private InterServerAssetUpdateService assetUpdateService;

    @Autowired
    private AccountCreateService accountCreateService;


    /* ---------- 정상 응답 ---------- */
    @Test
    @DisplayName("자산 응답 수신 후 → DB에 이력 저장 및 유저 상태 반영")
    void should_persist_asset_history_and_update_user() {
        // given
        // 1. '오늘' 날짜로 Clock을 설정하여 테스트 유저를 생성합니다.
        // 이 시점에 AccountCreateService가 AssetHistoryService를 통해 '오늘' 날짜의 초기 자산 이력을 생성합니다.
        LocalDate today = LocalDate.now();
        clockHelper.setClock(today);
        User user = accountCreateService.createUserWithDefaultHistories("test@email.com");

        long updatedAsset = 7777L;
        UpdateAssetResponse fakeResponse = new FakeUpdateAssetResponse(user.getId(), updatedAsset);

        // 2. 이제 테스트하려는 시점인 '내일'로 Clock을 다시 설정합니다.
        LocalDate tomorrow = today.plusDays(1);
        clockHelper.setClock(tomorrow);

        when(apiExecutor.executeWithToken(any()))
                .thenAnswer(invocation -> {
                    Function<String, UpdateAssetResponse> fn = invocation.getArgument(0);
                    return fn.apply("fake.jwt.token");
                });

        when(assetClient.updateAsset(Mockito.anyLong(), anyString(), any()))
                .thenReturn(fakeResponse);

        // when
        //  파라미터 없이 원래의 메소드를 호출합니다. 내부의 AssetHistoryService는 위에서 설정한 '내일' 날짜를 사용하게 됩니다.
        assetUpdateService.updateAllActiveUsersAsset();

        // then
        // 1. 자산 이력 저장 확인
        List<AssetHistory> histories = assetHistoryRepository.findAll();
        assertThat(histories).hasSize(2); // 초기 이력(오늘) + 갱신된 이력(내일)
        assertThat(histories).anyMatch(h ->
                h.getAmount() == updatedAsset && h.getSnapshotDay().equals(tomorrow)
        );

        // 2. 유저의 현재 자산 변경 확인
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getCurrentAssetHistory().getAmount()).isEqualTo(updatedAsset);
        assertThat(updatedUser.getCurrentAssetHistory().getSnapshotDay()).isEqualTo(tomorrow);
    }


    /* ---------- 부분 실패 격리 ---------- */
    @Test
    @DisplayName("여러 유저 중 일부 실패해도 나머지는 저장된다")
    void partial_failures_are_isolated() {
        // given: '오늘' 날짜로 유저 생성
        LocalDate today = LocalDate.now();
        clockHelper.setClock(today);

        User u1 = accountCreateService.createUserWithDefaultHistories("u1@x.com");
        User u2 = accountCreateService.createUserWithDefaultHistories("u2@x.com");

        // when: '내일' 날짜로 자산 업데이트 시도
        LocalDate tomorrow = today.plusDays(1);
        clockHelper.setClock(tomorrow);

        when(apiExecutor.executeWithToken(any())).thenAnswer(inv -> {
            Function<String, UpdateAssetResponse> fn = inv.getArgument(0);
            return fn.apply("jwt");
        });
        when(assetClient.updateAsset(eq(u1.getId()), anyString(), any()))
                .thenReturn(new FakeUpdateAssetResponse(u1.getId(), 1111L));
        when(assetClient.updateAsset(eq(u2.getId()), anyString(), any()))
                .thenThrow(new RuntimeException("ext down"));

        // 오늘 기준으로 생성된 유저의 정보를 내일로 갱신 (for Test)
        assetUpdateService.updateAllActiveUsersAsset();

        // then: u1만 성공적으로 갱신되었는지 확인
        assertThat(assetHistoryRepository.findAll())
                .anyMatch(h -> h.getUser().getId().equals(u1.getId()) && h.getAmount() == 1111L && h.getSnapshotDay().equals(tomorrow));

        // u2는 currentAssetHistory가 그대로 '오늘' 날짜인지
        User u2Reload = userRepository.findById(u2.getId()).orElseThrow();
        assertThat(u2Reload.getCurrentAssetHistory().getAmount())
                .isEqualTo(u2.getCurrentAssetHistory().getAmount());
        assertThat(u2Reload.getCurrentAssetHistory().getSnapshotDay()).isEqualTo(today);
    }
}
