package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistoryRepository;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.utils.Print;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

    private long defaultAsset;
    private String defaultUserName;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        assetHistoryRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 유저 생성
        User user = accountCreateService.createUserWithDefaultHistories("test@email.com");
        defaultAsset = user.getCurrentAssetHistory().getAmount(); // 10000000L
        defaultUserName = user.getUserAsset();
    }


    @Test
    @DisplayName("전체 흐름 통합 테스트: JWT 발급 → 확장 서버 → DB 반영")
    void should_update_user_asset_and_history_via_api() {
        // when
        updateService.updateAllActiveUsersAsset();

        // then
        User user = userRepository.findAll().getFirst();

        assertThat(user.getCurrentAssetHistory().getAmount())
                .isNotEqualTo(defaultAsset); // 자산이 갱신되었는지 확인

        assertThat(assetHistoryRepository.findAll())
                .hasSize(2); // 기존 1건 + 새로운 이력 1건

        Print.log(defaultAsset + "", "갱신 전 자산" );
        Print.log(user.getCurrentAssetHistory().getAmount()+"", "갱신 후 자산" );


        Print.log(defaultUserName + "", "갱신 유저 이름" );
        Print.log(user.getCurrentAssetHistory().getAssetName()+"", "갱신 후 유저 이름" );
    }
}
