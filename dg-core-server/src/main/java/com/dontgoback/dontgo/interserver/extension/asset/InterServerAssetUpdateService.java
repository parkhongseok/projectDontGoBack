package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.exception.UnauthorizedException;
import com.dontgoback.dontgo.interserver.auth.jwt.InterServerApiExecutor;
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


    /** 전체 활성 유저 자산 갱신 + 집계 결과 반환 */
    public BatchResult updateAllActiveUsersAsset() {
        long started = System.currentTimeMillis();
        List<User> activeUsers = userService.getActiveUsers();

        int total = activeUsers.size();
        int success = 0;

        for (User user : activeUsers) {
            try {
                UpdateAssetRequest req = new UpdateAssetRequest(user.getCurrentAssetHistory().getAmount());
                UpdateAssetResponse res = apiExecutor.executeWithToken(jwt ->
                        assetClient.updateAsset(user.getId(), jwt, req)
                );

                persistAssetChange(user, res.getAsset());
                success++;

                log.info("유저 [id: {}] 자산 갱신 완료: {}", user.getId(), res.getAsset());
            } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
                // ★ ApiExecutor가 캐치할 수 있도록 우리 도메인 예외로 매핑
                throw new UnauthorizedException("401 from extension-server", e);
            } catch (UnauthorizedException e) {
                throw e;
            } catch (Exception e) {
                log.warn("유저 [id: {}] 자산 갱신 실패: {}", user.getId(), e.getMessage());
            }
        }

        return BatchResult.builder()
                .total(total)
                .success(success)
                .failed(total - success)
                .elapsedMs(System.currentTimeMillis() - started)
                .build();
    }

    @Transactional
    public void persistAssetChange(User user, long updatedAsset) {
        AssetHistory history = AssetHistory.of(user, updatedAsset);
        assetHistoryRepository.save(history);

        user.setCurrentAssetHistory(history);
        userService.save(user); // 또는 userRepository.save(user)
    }
}
