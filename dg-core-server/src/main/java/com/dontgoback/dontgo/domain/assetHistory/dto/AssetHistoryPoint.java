package com.dontgoback.dontgo.domain.assetHistory.dto;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 단일 데이터 포인트 DTO: 차트의 한 점(하루/주/월 등 집계 단위)을 표현
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class AssetHistoryPoint {
    private LocalDate date;          // 해당 날짜
    private long amount;             // 해당 날짜 자산 금액(원)
    private Long changeAmount;       // 직전 대비 증감액(원). 첫 포인트는 null
    private Double changePercent;    // 직전 대비 증감률(퍼센트 값). 예: 1.2(%)
    private RedBlueType type;        // RED/BLUE 등 시각화용 분류
}
