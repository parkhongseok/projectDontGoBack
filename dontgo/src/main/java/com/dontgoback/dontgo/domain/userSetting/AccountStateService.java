package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.refreshToken.RefreshTokenService;
import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistoryService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCloseService {
    private final RefreshTokenService refreshTokenService;
    private final AccountStatusHistoryService accountStatusHistoryService;

    @Transactional
    public void closeAccount(User user) {
        // 토큰 삭제
        refreshTokenService.deleteByUserId(user.getId());
        // 유저의 토큰도 만료시켜야하나? 근데 DB에서 지웠으면 끝 아님?

        // 계정 상태 전환
        accountStatusHistoryService.updateStatus(user, AccountStatus.CLOSE_REQUESTED, "탈퇴 요청");
    }

    @Transactional
    public void deactivateAccount(User user) {
        // 토큰 삭제
        refreshTokenService.deleteByUserId(user.getId());
        // 계정 상태 전환
        accountStatusHistoryService.updateStatus(user, AccountStatus.INACTIVE, "비활성화");
    }
}
