package com.dontgoback.dontgo.batch.asset.job;

import com.dontgoback.dontgo.batch.asset.usecase.AssetRefreshUseCase;
import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/** job 역할
 * 여러 스텝/유스케이스를 순서대로 조립하고,
 * 집계/타이밍/후처리 같은 배치 오케스트레이션을 맡는다
 * 선행 조건 체크(예: 오늘 이미 실행했는지)
 * 여러 유스케이스 호출 (예: 자산 갱신 → 요약 리포팅)
 * 실행 시간/결과 집계(BatchResult) 구성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AssetRefreshJob {
    private final AssetRefreshUseCase useCase;

    public BatchResult run(LocalDate snapshotDay) {
        log.info("📦 배치 실행: snapshotDay = {}", snapshotDay);
        return useCase.refreshAllActiveUsers(snapshotDay); // 내부에서 total/success/failed/elapsed 집계 후 반환
    }

}
