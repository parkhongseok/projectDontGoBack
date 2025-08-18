package com.dontgoback.dontgo.interserver.extension.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssetResponse {
    private long userId;
    private long originalAsset;
    private double multiplier;     // 적용된 일일 배수 (예: 0.985 ~ 1.05)
    private long updatedAsset;
    private String date;           // YYYY-MM-DD (기준 타임존)
}