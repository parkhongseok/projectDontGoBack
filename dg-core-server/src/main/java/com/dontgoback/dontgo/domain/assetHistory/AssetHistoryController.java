package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistorySeriesResponse;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users/{userId}/asset-histories")
@RequiredArgsConstructor
public class AssetHistoryController {

    private final AssetSeriesService assetSeriesService;

    @GetMapping
    public ResponseEntity<?> getSeries(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "daily") String interval,   // daily | weekly | monthly
            @RequestParam(required = false) Integer limit,            // 초과 시 데시메이션
            @RequestHeader(value = "If-None-Match", required = false) String ifNoneMatch
    ) {
        // 1) 기본 유효성 검증
        if (from.isAfter(to)) {
            ResData<Void> body = ResData.of("F-400", "`from` 날짜가 `to` 보다 늦을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .cacheControl(CacheControl.noCache())
                    .body(body);
        }

        // 2) 서비스 호출
        AssetHistorySeriesResponse series =
                assetSeriesService.getSeries(userId, from, to, interval, limit);

        // 3) ETag 생성 (요청 조건 + 최신 변경시각 기반)
        String etag = assetSeriesService.buildEtag(
                userId, series.getFrom(), series.getTo(), series.getInterval(), series.getLatestChangedAt()
        );

        // 4) 304 Not Modified 처리 (본문 없음)
        if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED)
                    .eTag(etag)
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        // 5) 성공(200) - ResData 래핑
        ResData<AssetHistorySeriesResponse> body = ResData.of("S-200", "자산 시계열을 반환합니다.", series);
        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(CacheControl.noCache())
                .body(body);
    }
}