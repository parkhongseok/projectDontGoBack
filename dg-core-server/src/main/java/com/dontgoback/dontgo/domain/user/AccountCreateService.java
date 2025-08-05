package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistory;
import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistoryService;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistory;
import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountCreateService {
    private final UserRepository userRepository;
    private final AccountStatusHistoryService accountStatusHistoryService;
    private final AssetHistoryService assetHistoryService;
    private final long DEFAULT_USER_ASSET = 10000000;

    @Transactional
    public User createUserWithDefaultHistories(String email) {
        User user = User.builder()
                .email(email)
                .build();
        userRepository.save(user);

        // 자산 이력 생성
        AssetHistory assetHistory = assetHistoryService.create(user, DEFAULT_USER_ASSET);
        user.setCurrentAssetHistory(assetHistory);

        // 계정 상태 이력 직접 생성
        AccountStatusHistory newStatusHistory = accountStatusHistoryService.create(user, AccountStatus.ACTIVE, "신규 가입" );
        user.setCurrentStatusHistory(newStatusHistory);

        return userRepository.save(user);
    }

    @Transactional
    public User tryUpdateUserWithAccountHistories(User user){
        AccountStatus currentStatus = Optional.ofNullable(user.getCurrentStatusHistory())
                .map(AccountStatusHistory::getAccountStatus)
                .orElse(null);

        String reason = "오류";
        if (currentStatus == AccountStatus.CLOSE_REQUESTED) {
            reason = "탈퇴 철회";
        } else if (currentStatus == AccountStatus.INACTIVE) {
            reason = "비활성화 해제";
        }
        AccountStatusHistory newStatusHistory = accountStatusHistoryService.create(user, AccountStatus.ACTIVE, reason);
        user.setCurrentStatusHistory(newStatusHistory);

        return userRepository.save(user);
    }
}
