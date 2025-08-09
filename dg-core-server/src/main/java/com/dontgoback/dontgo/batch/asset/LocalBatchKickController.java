package com.dontgoback.dontgo.batch.asset;

import com.dontgoback.dontgo.batch.asset.job.AssetRefreshJob;
import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/internal/batch")
public class LocalBatchKickController {
    private final AssetRefreshJob job;

    @GetMapping("/asset-refresh/run")
    public BatchResult runNow() {
        return job.run(); // 내부에서 InterServer 호출 + DB 반영
    }
}

// curl -X POST http://localhost:8090/test/internal/batch/asset-refresh/run