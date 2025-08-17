package com.dontgoback.dontgo.seed.asset;

import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryRepository;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetHistorySeedTx {

    private final UserRepository userRepo;
    private final AssetHistoryRepository historyRepo;
    @PersistenceContext
    EntityManager em;

    /**
     * 시작일엔 1천만으로 고정, 그 다음날부터 multiplier 적용.
     * 같은 (userId, day)가 이미 있으면 amount/multiplier만 업데이트(덮어쓰기).
     */
    @Transactional
    public void seedOverwrite(long startAmount,
                              int daysBackInclusive,   // 예: 30 → start ~ today (오늘 포함)
                              double sigma, double minPct, double maxPct, String salt) {

        LocalDate today = LocalDate.now();
        LocalDate start = today.minusDays(daysBackInclusive);

        List<User> users = userRepo.findAll();
        int touched = 0;

        for (User u : users) {
            Long uid = u.getId();
            if (uid == null) continue;

            long prev = startAmount;

            for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {

                final double multiplier;
                final long amount;

                if (d.equals(start)) {
                    // 고정 시작점
                    multiplier = 1.0;
                    amount = startAmount;
                } else {
                    multiplier = DailyAssetMultiplier.generate(uid, d, sigma, minPct, maxPct, salt);
                    amount = Math.max(0L, Math.round(prev * multiplier));
                }

                // 업서트: 있으면 수정, 없으면 생성
                Optional<AssetHistory> existing = historyRepo.findByUserIdAndSnapshotDay(uid, d);
                if (existing.isPresent()) {
                    AssetHistory row = existing.get();
                    row.setAmount(amount);
                    row.setMultiplier(multiplier);
                    // snapshotDay는 동일, user도 동일
                    // changedAt은 @CreatedDate라면 새로 갱신되지 않음 → 별도 updatedAt 쓰는게 이상적
                    em.merge(row);
                } else {
                    em.persist(AssetHistory.of(u, amount, d, multiplier));
                }

                prev = amount;
                if (++touched % 500 == 0) { em.flush(); em.clear(); }
            }
        }
        em.flush(); em.clear();
    }


}