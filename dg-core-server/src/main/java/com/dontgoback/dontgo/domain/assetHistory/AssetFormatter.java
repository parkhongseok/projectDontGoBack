package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;

public class AssetFormatter {
    private static final String[] UNITS = {"원", "만", "억", "조"};
    private static final long[] VALUES = {1, 10_000, 100_000_000, 1_0000_0000_0000L};

    public static String getNameFormat(long amount){
        if (amount == 0) return "0원";

        long absAmount = Math.abs(amount); // 음수 처리



        String result = "";
        for (int i = VALUES.length - 1; i >= 0; i--) {
            if (absAmount >= VALUES[i]) {
                double formatted = (double) absAmount / VALUES[i];

                // 10 이상이면 소수점 제거, 아니면 소수점 한 자리까지 표시
                if (formatted >= 10) {
                    result = String.format("%.0f%s", formatted, UNITS[i]);
                } else {
                    result = String.format("%.1f%s", formatted, UNITS[i]);
                }
                break; // 가장 큰 단위만 남기고 종료
            }
        }
        return amount<0  ? "-" + result : result;
    }

    public static RedBlueType getTypeFormat(Double multiplier) {
        return multiplier >= 1 ? RedBlueType.BLUE : RedBlueType.RED;
    }
}