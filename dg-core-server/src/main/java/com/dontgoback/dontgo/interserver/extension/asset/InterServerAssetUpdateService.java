package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.interserver.auth.jwt.InterServerApiExecutor;
import com.dontgoback.dontgo.interserver.auth.jwt.InterServerTokenManager;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetRequest;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterServerAssetUpdateService {

    private final UserService userService;
    private final InterServerApiExecutor apiExecutor;
    private final InterServerAssetRequestService assetClient;
    private final AssetHistoryRepository assetHistoryRepository;

    public void updateAllActiveUsersAsset() {
        List<User> activeUsers = userService.getActiveUsers();

        for (User user : activeUsers) {
            try {
                UpdateAssetRequest req = new UpdateAssetRequest(user.getCurrentAssetHistory().getAmount());

                // InterServerApiExecutor를 통해 토큰 관리 및 요청 수행
                UpdateAssetResponse res = apiExecutor.executeWithToken(jwt ->
                        assetClient.updateAsset(user.getId(), jwt, req)
                );

                // 아래 부분은 Transaction 걸림 위는 외부 통신이라 제외함
                persistAssetChange(user, res.getAsset());

                // 로그
                log.info("유저 [id: {}]의 자산 갱신 완료: {}", user.getId(), res.getAsset());

            } catch (Exception e) {
                log.warn("유저 [id: {}]의 자산 갱신 실패: {}", user.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void persistAssetChange(User user, long updatedAsset) {
        AssetHistory history = AssetHistory.of(user, updatedAsset);
        assetHistoryRepository.save(history);

        user.setCurrentAssetHistory(history);
        userService.save(user); // 또는 userRepository.save(user)
    }
}
