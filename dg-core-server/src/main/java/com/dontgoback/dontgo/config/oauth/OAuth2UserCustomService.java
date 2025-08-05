package com.dontgoback.dontgo.config.oauth;


import com.dontgoback.dontgo.domain.user.AccountCreateService;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    private final AccountCreateService accountCreateService;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        saveOrUpdateUser(oAuth2User);
        return oAuth2User;
    }

    private User saveOrUpdateUser(OAuth2User oAuth2User) {
        String email = (String) oAuth2User.getAttributes().get("email");
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()){
            return updateUser(user.get());
        }else{
            return createNewUser(email);
        }
    }

    private User updateUser(User user){
        return accountCreateService.tryUpdateUserWithAccountHistories(user);
    }

    private User createNewUser(String email){
        return accountCreateService.createUserWithDefaultHistories(email);
    }



}
