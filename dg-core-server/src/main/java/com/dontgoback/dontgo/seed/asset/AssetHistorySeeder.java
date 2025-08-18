package com.dontgoback.dontgo.seed.asset;

import com.dontgoback.dontgo.seed.core.SeedHelper;
import com.dontgoback.dontgo.seed.core.SeedProfiles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;



/** 사전 작업
 * DB
 *  create table if not exists data_seed_lock (
 *  lock_key varchar(128) primary key,
 *  created_at timestamp default CURRENT_TIMESTAMP );
 * 제약조건 추가
 *  alter table asset_history add constraint if not exists uk_asset_history_user_snapshot_day
 *  unique (user_id, snapshot_day);
 * 실행
 * SPRING_PROFILES_ACTIVE=seed ./gradlew :dg-core-server:bootRun
 * 또는
 * SPRING_PROFILES_ACTIVE=seed java -jar dg-core-server.jar
 */
@Slf4j
@Component
@Profile(SeedProfiles.SEED)
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE)
public class AssetHistorySeeder implements CommandLineRunner {

    private final SeedHelper seedHelper;
    private final AssetHistorySeedTx seedTx;

    @Value("${seed.enabled:false}") private boolean enabled;
    @Value("${seed.days:30}")       private int days;                   // 오늘 포함 되돌릴 일수
    @Value("${seed.startAmount:10000000}") private long startAmount;    // 1천만 기본
    @Value("${seed.asset.volatility.sigma:0.02}")     private double sigma;
    @Value("${seed.asset.clamp.minPercent:-5.0}")     private double minPct;
    @Value("${seed.asset.clamp.maxPercent:5.0}")      private double maxPct;
    @Value("${seed.asset.secretSalt:secretSalt}")      private String salt;

    private static final String LOCK_KEY = "asset_history_seed_v2"; // 🔁 버전 올려 새로운 시드 허용

    @Override
    public void run(String... args) {
        if (!enabled) { log.info("[SEED] disabled. skip."); return; }
        if (minPct > maxPct) throw new IllegalArgumentException("minPercent <= maxPercent");
        if (sigma < 0)       throw new IllegalArgumentException("sigma >= 0");

        seedHelper.runOnce(LOCK_KEY, () ->
                seedTx.seedOverwrite(startAmount, days, sigma, minPct, maxPct, salt)
        );
        System.exit(0); // ✅ 작업 완료 후 정상 종료 코드 필수
    }
}