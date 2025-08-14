package com.dontgoback.dontgo.domain.assetHistory;


import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistoryPoint;
import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistorySeriesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AssetSeriesService {

    private final AssetHistoryRepository repo;

    @Transactional(readOnly = true)
    public AssetHistorySeriesResponse getSeries(
            Long userId, LocalDate from, LocalDate to, String interval, Integer limit
    ) {
        String iv = interval == null ? "daily" : interval.toLowerCase();
        int max = (limit == null || limit <= 0) ? 1000 : limit;

        List<AssetHistory> raw = repo.findRange(userId, from, to);
        LocalDateTime latest = this.getLatestChangedAt(userId);

        List<AssetHistory> rolled = switch (iv) {
            case "weekly"  -> rollupWeeklyLast(raw);
            case "monthly" -> rollupMonthlyLast(raw);
            default        -> raw; // daily
        };

        List<AssetHistoryPoint> points = toPointsWithChanges(rolled);

        if (points.size() > max) {
            points = SimpleDecimator.even(points, max);
        }

        return AssetHistorySeriesResponse.builder()
                .userId(userId)
                .from(from).to(to)
                .interval(iv)
                .points(points)
                .latestChangedAt(latest)
                .count(points.size())
                .build();
    }

    /* ----- 집계: 주/월 ‘마지막값(last)’ ----- */

    private List<AssetHistory> rollupWeeklyLast(List<AssetHistory> rows) {
        Map<LocalDate, List<AssetHistory>> byWeek = new HashMap<>();
        for (AssetHistory a : rows) {
            LocalDate weekStart = a.getSnapshotDay()
                    .with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            byWeek.computeIfAbsent(weekStart, k -> new ArrayList<>()).add(a);
        }
        List<AssetHistory> result = new ArrayList<>();
        for (List<AssetHistory> list : byWeek.values()) {
            list.sort(Comparator.comparing(AssetHistory::getSnapshotDay));
            result.add(list.get(list.size() - 1));
        }
        result.sort(Comparator.comparing(AssetHistory::getSnapshotDay));
        return result;
    }

    private List<AssetHistory> rollupMonthlyLast(List<AssetHistory> rows) {
        Map<YearMonth, List<AssetHistory>> byYm = new HashMap<>();
        for (AssetHistory a : rows) {
            YearMonth ym = YearMonth.from(a.getSnapshotDay());
            byYm.computeIfAbsent(ym, k -> new ArrayList<>()).add(a);
        }
        List<AssetHistory> result = new ArrayList<>();
        for (List<AssetHistory> list : byYm.values()) {
            list.sort(Comparator.comparing(AssetHistory::getSnapshotDay));
            result.add(list.get(list.size() - 1));
        }
        result.sort(Comparator.comparing(AssetHistory::getSnapshotDay));
        return result;
    }

    /* ----- 엔티티 -> 포인트 + 변화량 계산 ----- */

    private List<AssetHistoryPoint> toPointsWithChanges(List<AssetHistory> rows) {
        List<AssetHistoryPoint> out = new ArrayList<>(rows.size());
        AssetHistory prev = null;

        for (AssetHistory cur : rows) {
            Long changeAmount = null;
            Double changePercent = null;

            if (prev != null) {
                long diff = cur.getAmount() - prev.getAmount();
                changeAmount = diff;
                if (prev.getAmount() != 0L) {
                    changePercent = toPercent(diff, prev.getAmount()); // 예: 1.23 (%)
                }
            }

            out.add(AssetHistoryPoint.builder()
                    .date(cur.getSnapshotDay())
                    .amount(cur.getAmount())
                    .changeAmount(changeAmount)
                    .changePercent(changePercent)
                    .type(cur.getAssetType())
                    .build());

            prev = cur;
        }
        return out;
    }

    /** (diff / base) * 100 → 소수점 둘째 자리 반올림 */
    private Double toPercent(long diff, long base) {
        java.math.BigDecimal d = java.math.BigDecimal.valueOf(diff);
        java.math.BigDecimal b = java.math.BigDecimal.valueOf(base);
        java.math.BigDecimal pct = d.divide(b, 6, java.math.RoundingMode.HALF_UP)
                .multiply(java.math.BigDecimal.valueOf(100));
        return pct.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue();
    }

    /* ----- (선택) ETag 만들 때 컨트롤러에서 호출 ----- */

    /**
     * ETag 생성을 위해 데이터의 최종 변경 시각을 조회합니다.
     * @param userId 사용자 ID
     * @return 최종 변경 시각. 데이터가 없으면 null.
     */
    @Transactional(readOnly = true)
    public LocalDateTime getLatestChangedAt(Long userId) {
        return repo.findLatestChangedAt(userId);
    }

    public String buildEtag(Long userId, LocalDate from, LocalDate to, String interval, LocalDateTime latest) {
        String raw = userId + "|" + (interval == null ? "daily" : interval) + "|" + from + "|" + to + "|" +
                (latest == null ? "none" : latest.toString());
        return "\"asset-" + raw.hashCode() + "\"";
    }
}
