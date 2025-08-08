package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetHistoryService {
    private final AssetHistoryRepository assetHistoryRepository;

    public AssetHistory create(User user, long amount) {
        AssetHistory history = AssetHistory
                .builder()
                .user(user)
                .amount(amount)
                .changedAt(LocalDateTime.now())
                .build();

        return assetHistoryRepository.save(history);
    }

}
