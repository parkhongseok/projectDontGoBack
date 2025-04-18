package com.dontgoback.dontgo.config.oauth;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserAsset;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistory;
import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistoryService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final AccountStatusHistoryService accountStatusHistoryService;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        saveOrUpdateUser(oAuth2User);
        return oAuth2User;
    }

    private User saveOrUpdateUser(OAuth2User oAuth2User) {
        String email = (String) oAuth2User.getAttributes().get("email");

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            AccountStatus currentStatus = Optional.ofNullable(user.getCurrentStatusHistory())
                    .map(AccountStatusHistory::getAccountStatus)
                    .orElse(null); // 이 부분 처

            if (currentStatus == AccountStatus.CLOSE_REQUESTED) {
                accountStatusHistoryService.updateStatus(user, AccountStatus.ACTIVE, "탈퇴 철회");
            } else if (currentStatus == AccountStatus.INACTIVE) {
                accountStatusHistoryService.updateStatus(user, AccountStatus.ACTIVE, "비활성화 해제");
            }

            return user;
        }

        // 신규 유저 등록
        return createUserWithActiveStatus(email);
    }

    private User createUserWithActiveStatus(String email) {
        UserAsset userAsset = new UserAsset(); // 랜덤 생성
        User user = User.builder()
                .email(email)
                .userAsset(userAsset.getUserAssetName())
                .userType(userAsset.getUserAssetType())
                .build();

        User savedUser = userRepository.save(user);

        return accountStatusHistoryService.initializeStatus(savedUser, AccountStatus.ACTIVE, "신규 가입");
    }
}
