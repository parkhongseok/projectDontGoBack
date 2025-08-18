package com.dontgoback.dontgo.seed.asset;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.SplittableRandom;

/** 하루 단위 자산 배수 생성기: (userId, 날짜, salt) → multiplier */
public final class DailyAssetMultiplier {

    public static double generate(long userId, LocalDate day,
                                  double sigma, double minPercent, double maxPercent,
                                  String secretSalt) {
        long seed = hmacToLong(userId, day, secretSalt);
        SplittableRandom rnd = new SplittableRandom(seed);

        // 개방구간 (0,1)
        double u1 = nextOpen01(rnd);
        double u2 = nextOpen01(rnd);
        // Box–Muller
        double z = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);

        double mu = -0.5 * sigma * sigma;
        double raw = Math.exp(mu + sigma * z);

        double minMul = 1.0 + minPercent / 100.0;
        double maxMul = 1.0 + maxPercent / 100.0;
        return Math.min(maxMul, Math.max(minMul, raw));
    }

    private static long hmacToLong(long userId, LocalDate day, String salt) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String data = userId + "|" + day;
            byte[] h = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.wrap(h).getLong();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static double nextOpen01(SplittableRandom r) {
        double d;
        do { d = r.nextDouble(); } while (d == 0.0);
        return d;
    }

    private DailyAssetMultiplier() {}
}