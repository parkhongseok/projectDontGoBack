//package com.dontgoback.dontgo.config.oauth;
//
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.domain.user.UserAsset;
//import com.dontgoback.dontgo.domain.user.UserRepository;
//import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistory;
//import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistoryRepository;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.Optional;
//
//@RequiredArgsConstructor
//@Service
//public class OAuth2UserCustomService2 extends DefaultOAuth2UserService {
//    private final UserRepository userRepository;
//    private final AccountStatusHistoryRepository accountStatusHistoryRepository;
//
//    // 구글 -> 서비스 서버(여기) : 사용자 정보 불러오기 : 사용자 조회 후 있으면 업데이트 신규면 저장
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
//        // 요청을 받으면, 해당 정보를 바탕으로 유저 객체를 반환
//        // OAuth 서비스에서 재공하는 정보를 기반으로 유저 객체 생성
//        OAuth2User user = super.loadUser(userRequest);
//        saveOrUpdate(user);
//        return user;
//    }
//
//    // 서비스 서버 -> 서비스 DB
//    // 유저가 있다면 업데이트, 없으면 유저 생성
//    // 위에서 오버라이드한 DefaultOAuth2UserService의 로드 유저를 통해, 아래 메서드의 파라미터로 들어올 유저 객체를 불러옴
//    // 필요 시 구글에서 가져온 데이터를 사용하여 사진 등을 활용할 수도 있을듯?
//    // getAttributes에서 뭔가 더 꺼내는 방식으로 말이지
//    private User saveOrUpdate(OAuth2User oAuth2User) {
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = (String) attributes.get("email");
////        String name = (String) attributes.get("name");
//
//        UserAsset userAsset = new UserAsset(); // amount 값은 따로 history에 저장, 임시 이름
//
//        Optional<User> optionalUser = userRepository.findByEmail(email);
//
//        // 기존 유저
//        if (optionalUser.isPresent()) {
//            // 유저의 history 상태 확인 후, 정지인 경우엔 아마 로그인 불가,
//            // 비활성화 및 정지 대기인 경우 상태를 활성화로 변경
//            User user = optionalUser.get();
//            AccountStatus status = user.getCurrentStatusHistory().getAccountStatus();
//            AccountStatusHistory activeStatus;
//
//            if (status == AccountStatus.CLOSE_REQUESTED) {
//                // 계정 상태 이력 추가: ACTIVE
//                activeStatus = AccountStatusHistory.builder()
//                        .user(user)
//                        .accountStatus(AccountStatus.ACTIVE)
//                        .changedAt(LocalDateTime.now())
//                        .reason("계정 삭제 철회")
//                        .build();
//            }else if (status == AccountStatus.INACTIVE) {
//                // 계정 상태 이력 추가: ACTIVE
//                activeStatus = AccountStatusHistory.builder()
//                        .user(user)
//                        .accountStatus(AccountStatus.ACTIVE)
//                        .changedAt(LocalDateTime.now())
//                        .reason("계정 활성화")
//                        .build();
//            }else{
//                return user;  // 기존 유저는 그대로 반환
//            }
//            accountStatusHistoryRepository.save(activeStatus);
//            user.setCurrentStatusHistory(activeStatus);
//            userRepository.save(user);
//            return user;
//        }
//
//        // 신규 유저 생성
//        User newUser = userRepository.save(
//                User.builder()
//                        .email(email)
//                        .userAsset(userAsset.getUserAssetName())
//                        .userType(userAsset.getUserAssetType())
//                        .build()
//        );
//
//        // 계정 상태 이력 추가: ACTIVE
//        AccountStatusHistory activeStatus = AccountStatusHistory.builder()
//                .user(newUser)
//                .accountStatus(AccountStatus.ACTIVE)
//                .changedAt(LocalDateTime.now())
//                .reason("신규 가입")
//                .build();
//
//        accountStatusHistoryRepository.save(activeStatus);
//
//        newUser.setCurrentStatusHistory(activeStatus);
//        userRepository.save(newUser); // 영속 상태로 만들어서 리턴해야함
//
//        return newUser;
//    }
//}
