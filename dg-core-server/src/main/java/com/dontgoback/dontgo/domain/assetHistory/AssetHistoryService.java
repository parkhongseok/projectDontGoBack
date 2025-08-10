package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetHistoryService {
    private final AssetHistoryRepository assetHistoryRepository;
    private final Clock clock;

    /** 핵심: 특정 날짜 스냅샷 저장 */
    @Transactional
    public AssetHistory create(User user, long amount, Double multiplier) {
        LocalDate snapshotDay = LocalDate.now(clock);

        if (user == null) throw new IllegalArgumentException("user must not be null");
        if (amount < 0) throw new IllegalArgumentException("amount must be >= 0");

        AssetHistory history = AssetHistory.of(user, amount, snapshotDay, multiplier);

        // 단순 insert만 한다면 save; 하루 1건 보장(유니크) 필요 시 이 지점에서 upsert/존재시 update로 확장
        return assetHistoryRepository.save(history);
    }

    public AssetHistory create(User user, long amount){
        return create(user, amount, null);
    }

}
