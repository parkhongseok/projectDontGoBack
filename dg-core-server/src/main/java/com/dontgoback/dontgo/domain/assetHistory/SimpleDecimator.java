package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistoryPoint;

import java.util.List;

public class SimpleDecimator {
    private SimpleDecimator() {}

    /** 균등 샘플링: 첫/마지막은 항상 포함, 중간은 간격으로 선택 */
    public static List<AssetHistoryPoint> even(List<AssetHistoryPoint> input, int limit) {
        if (input.size() <= limit) return input;
        if (limit < 2) return java.util.List.of(input.get(0));

        java.util.List<AssetHistoryPoint> out = new java.util.ArrayList<>(limit);
        out.add(input.get(0));

        double step = (double) (input.size() - 1) / (limit - 1);
        for (int i = 1; i < limit - 1; i++) {
            int idx = (int) Math.round(i * step);
            if (idx <= 0) idx = 1;
            if (idx >= input.size() - 1) idx = input.size() - 2;
            out.add(input.get(idx));
        }

        out.add(input.get(input.size() - 1));

        // 날짜 중복 제거 + 정렬(안전)
        java.util.Map<java.time.LocalDate, AssetHistoryPoint> map =
                new java.util.TreeMap<>();
        for (AssetHistoryPoint p : out) map.putIfAbsent(p.getDate(), p);
        return new java.util.ArrayList<>(map.values());
    }
}
