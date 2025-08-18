package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistoryRepository;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.interserver.extension.asset.dto.FakeUpdateAssetResponse;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetResponse;
import com.dontgoback.dontgo.interserver.extension.TestClockConfig;
import com.dontgoback.dontgo.util.ClockHelper;
import com.dontgoback.dontgo.util.Print;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;
import com.dontgoback.dontgo.interserver.auth.jwt.InterServerApiExecutor;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestClockConfig.class) // 테스트 설정을 가져옵니다.
public class AssetUpdateIntegrationTest {

    @Autowired
    private InterServerAssetUpdateService updateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssetHistoryRepository assetHistoryRepository;

    @Autowired
    private AccountStatusHistoryRepository statusHistoryRepository;

    @Autowired
    private AccountCreateService accountCreateService;

    @Autowired
    private ClockHelper clockHelper;

    // 3. 외부 API 요청을 보내는 서비스를 MockBean으로 대체
    @MockBean
    private InterServerApiExecutor apiExecutor;

    private long defaultAsset;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 1. 데이터 정합성을 위해 자식 테이블부터 순서대로 삭제
        statusHistoryRepository.deleteAll();
        assetHistoryRepository.deleteAll();
        userRepository.deleteAll();

        // '오늘' 날짜로 Clock을 설정하여 초기 상태를 만듭니다.
        LocalDate today = LocalDate.now();
        clockHelper.setClock(today);

        // 테스트용 유저 생성
        testUser = accountCreateService.createDefaultAccount("test@email.com");
        defaultAsset = testUser.getCurrentAssetHistory().getAmount(); // 10000000L
    }


    @Test
    @DisplayName("전체 흐름 통합 테스트: JWT 발급 → 확장 서버 → DB 반영")
    void should_update_user_asset_and_history_via_api() {
        // given: 외부 API가 특정 값을 반환하도록 Mocking
        long expectedUpdatedAsset = 99_999_999L;
        UpdateAssetResponse fakeResponse = new FakeUpdateAssetResponse(testUser.getId(), expectedUpdatedAsset, 1.5);

        // when: '내일' 날짜로 Clock을 설정하여 상태 변경을 시뮬레이션합니다.
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        clockHelper.setClock(tomorrow);

        // InterServerApiExecutor를 Mocking하여 실제 JWT 발급 및 API 호출을 막습니다.
        // executeWithToken이 어떤 함수를 받든지 간에 미리 정의된 fakeResponse를 반환하도록 설정합니다.
        when(apiExecutor.executeWithToken(any()))
                .thenReturn(fakeResponse);

        // when
        updateService.updateAllActiveUsersAsset();

        // then
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();

        // 4. Mocking된 값으로 정확히 갱신되었는지 명확하게 검증
        assertThat(updatedUser.getCurrentAssetHistory().getAmount()).isEqualTo(expectedUpdatedAsset);

        // 5. 자산 이력이 총 2건(초기 1건 + 갱신 1건)인지 확인
        assertThat(assetHistoryRepository.findAllByUserId(testUser.getId())).hasSize(2);

        Print.log(defaultAsset + "", "갱신 전 자산" );
        Print.log(updatedUser.getCurrentAssetHistory().getAmount()+"", "갱신 후 자산" );
        Print.log(updatedUser.getCurrentAssetHistory().getAssetName()+"", "갱신 후 자산 이름" );
    }
}
