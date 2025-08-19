package com.dontgoback.dontgo.batch.asset;

import com.dontgoback.dontgo.batch.asset.job.AssetRefreshJob;
import com.dontgoback.dontgo.batch.common.dto.BatchResult;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/internal/batch")
public class LocalBatchKickController {
    private final AssetRefreshJob job;

    @GetMapping("/asset-refresh/run")
    public BatchResult runNow(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate snapshotDay) {
        if (snapshotDay == null) {
            snapshotDay = LocalDate.now();
        }
        return job.run(snapshotDay); // snapshotDay 전달
    }

}

// curl http://localhost:8090/test/internal/batch/asset-refresh/run
// curl http://localhost:8090/test/internal/batch/asset-refresh/run?snapshotDay=2025-08-18