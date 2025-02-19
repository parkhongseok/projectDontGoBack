package com.dontgoback.dontgo.config.oauth;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    // 구글 -> 서비스 서버(여기) : 사용자 정보 불러오기 : 사용자 조회 후 있으면 업데이트 신규면 저장
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 요청을 받으면, 해당 정보를 바탕으로 유저 객체를 반환
        // OAuth 서비스에서 재공하는 정보를 기반으로 유저 객체 생성
        OAuth2User user = super.loadUser(userRequest);
        saveOrUpdate(user);
        return user;
    }

    // 서비스 서버 -> 서비스 DB
    // 유저가 있다면 업데이트, 없으면 유저 생성
    private User saveOrUpdate(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .userName("ME")
                        .userType(RedBlueType.BLUE)
                        .build());
        return userRepository.save(user);
    }
}
