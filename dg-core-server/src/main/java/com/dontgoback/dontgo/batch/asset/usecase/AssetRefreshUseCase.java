package com.dontgoback.dontgo.batch.asset.usecase;

import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import com.dontgoback.dontgo.interserver.extension.asset.InterServerAssetUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** usecase 역할
 *      하나의 비즈니스 흐름을 완결적으로 수행.
 *      도메인 서비스/리포지토리/외부 클라이언트를 조합한다.
 * 여기서 신경 쓸 것:
 *      트랜잭션 경계(외부 호출은 밖, DB 반영은 안)
 *      멱등성/부분 실패 처리/로깅
 */

// 정리: scheduler는 “언제”, job은 “무엇을 어떤 순서로”, application은 “어떻게(업무적으로)”를 담당.

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetRefreshUseCase {
    private final InterServerAssetUpdateService updateService;

    public BatchResult refreshAllActiveUsers() {
        return updateService.updateAllActiveUsersAsset();
    }
}