package com.dontgoback.dontgo.batch.common.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BatchResult {
    private int total;
    private int success;
    private int failed;
    private long elapsedMs;
}
