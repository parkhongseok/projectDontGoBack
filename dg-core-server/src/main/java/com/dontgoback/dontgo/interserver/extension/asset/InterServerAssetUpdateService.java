package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryService;
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

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterServerAssetUpdateService {

    private final UserService userService;
    private final InterServerApiExecutor apiExecutor;
    private final InterServerAssetRequestService assetClient;
    private final AssetHistoryService assetHistoryService;

    /** 전체 활성 유저 자산 갱신 + 집계 결과 반환*/
    public BatchResult updateAllActiveUsersAsset(LocalDate snapshotDay) {
        long started = System.currentTimeMillis();
        List<User> activeUsers = userService.getActiveUsers();

        int total = activeUsers.size();
        int success = 0;

        for (User user : activeUsers) {
            try {
                UpdateAssetRequest req = new UpdateAssetRequest(user.getCurrentAssetHistory().getAmount());
                UpdateAssetResponse res = apiExecutor.executeWithToken(jwt ->
                        assetClient.updateAsset(user.getId(), jwt, req, snapshotDay)
                );

                persistAssetChange(user, res.getUpdatedAsset(), res.getMultiplier());
                success++;

                log.info("유저 [id: {}] 자산 갱신 완료: {}", user.getId(), res.getUpdatedAsset());
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
    public void persistAssetChange(User user, long updatedAsset, Double multiplier) {
        AssetHistory history
                = assetHistoryService.create(user, updatedAsset, multiplier);
        user.setCurrentAssetHistory(history);
        userService.save(user);
    }
}
