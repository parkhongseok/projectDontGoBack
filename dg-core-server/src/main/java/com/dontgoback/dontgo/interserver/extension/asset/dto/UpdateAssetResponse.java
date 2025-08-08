package com.dontgoback.dontgo.interserver.extension.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAssetResponse {
    private long userId;
    private long asset;
}
