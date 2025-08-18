package com.dontgoback.dontgo.domain.accountStateHistory;

import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountStateService {
    private final RefreshTokenService refreshTokenService;
    private final AccountStatusHistoryService accountStatusHistoryService;
    private final UserRepository userRepository;

    @Transactional
    public void closeAccount(User user) {
        // 토큰 삭제
        refreshTokenService.deleteByUserId(user.getId());
        // 유저의 토큰도 만료시켜야하나? 근데 DB에서 지웠으면 끝 아님? ㅇㅇ

        // 계정 상태 전환
        AccountStatusHistory accountStatusHistory = accountStatusHistoryService.create(user, AccountStatus.CLOSE_REQUESTED, "탈퇴 요청");
        user.setCurrentStatusHistory(accountStatusHistory);
        userRepository.save(user);
    }

    @Transactional
    public void deactivateAccount(User user) {
        // 토큰 삭제
        refreshTokenService.deleteByUserId(user.getId());

        // 계정 상태 전환
        AccountStatusHistory accountStatusHistory = accountStatusHistoryService.create(user, AccountStatus.INACTIVE, "비활성화");
        user.setCurrentStatusHistory(accountStatusHistory);
        userRepository.save(user);
    }
}
