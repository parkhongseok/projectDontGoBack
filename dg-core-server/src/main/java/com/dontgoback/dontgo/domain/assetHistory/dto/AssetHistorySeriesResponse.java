package com.dontgoback.dontgo.domain.assetHistory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 차트 응답 DTO: 프론트가 그대로 렌더링할 수 있도록 시리즈 형태로 전달합니다.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssetHistorySeriesResponse {
    /** 데이터 주인(보안상 서버에서 세션 기반으로 강제 매핑 권장). */
    private Long userId;

    /** 쿼리 범위 시작/끝(요청 파라미터 에코백). */
    private LocalDate from;
    private LocalDate to;

    /** 응답 해상도: daily | weekly | monthly 등. */
    private String interval;

    /** 차트 포인트 배열(시간 오름차순). */
    private List<AssetHistoryPoint> points;

    /** 변경 이력의 최신 시각. ETag/Last-Modified 계산에 활용됩니다. */
    private LocalDateTime latestChangedAt;

    /** 총 포인트 수(디버깅/페이지네이션/데시메이션 판단에 유용). */
    private int count;
}
