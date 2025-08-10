package com.dontgoback.dontgo.interserver.extension.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class FakeUpdateAssetResponse extends UpdateAssetResponse{
    private long userId;
    private long updatedAsset;
    private Double mul;

    public FakeUpdateAssetResponse(long userId, long asset){
        super();
        this.userId = userId;
        this.updatedAsset = asset;
    }

    public FakeUpdateAssetResponse(long userId, long asset, Double mul){
        super();
        this.userId = userId;
        this.updatedAsset = asset;
        this.mul = mul;
    }
}
