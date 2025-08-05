package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.Getter;

import java.util.Random;

@Getter
public class UserAsset {
    private final long amount;
    private final RedBlueType userAssetType;
    private final String userAssetName;

    public UserAsset(){
        this.amount = setAsset();
        this.userAssetName = setFormatAssetName();
        this.userAssetType = setUserAssetType();
    }

    private long setAsset() {
        Random random = new Random();
        // 난수를 사용하여 -5000 ~ 5000 사이의 값을 생성 > 추후 api로 입력받을 예정
        int maxAmount = 1000000000;
        int minAmount = -1000000000;
        return random.nextInt(maxAmount - minAmount + 1) + minAmount;
    }

    private RedBlueType setUserAssetType(){
        return amount >= 0 ? RedBlueType.BLUE : RedBlueType.RED;
    }

    public String setFormatAssetName() {
        if (amount == 0) return "0원";

        long absAmount = Math.abs(amount); // 음수 처리

        String[] units = {"원", "만", "억", "조"};
        long[] values = {1, 10_000, 100_000_000, 1_0000_0000_0000L};

        String result = "";
        for (int i = values.length - 1; i >= 0; i--) {
            if (absAmount >= values[i]) {
                double formatted = (double) absAmount / values[i];

                // 10 이상이면 소수점 제거, 아니면 소수점 한 자리까지 표시
                if (formatted >= 10) {
                    result = String.format("%.0f%s", formatted, units[i]);
                } else {
                    result = String.format("%.1f%s", formatted, units[i]);
                }
                break; // 가장 큰 단위만 남기고 종료
            }
        }
        return amount<0  ? "-" + result : result;
    }
}
