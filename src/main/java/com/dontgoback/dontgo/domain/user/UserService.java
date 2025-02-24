package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.user.dto.LoginUserResponse;
import com.dontgoback.dontgo.global.resData.ResData;
import org.springframework.web.server.ResponseStatusException;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    public LoginUserResponse findMe(String userEmail) {
        User user = findByEmail(userEmail);
        return LoginUserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .userName(user.getUserAsset())
                .userType(user.getUserType())
                .build();
    }
    public User findById(Long userId){
        return userRepository.findById(userId)
                // 401 Unauthorized
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 실패. 올바른 사용자 정보가 아닙니다."));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                // 401 Unauthorized
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 실패. 올바른 사용자 정보가 아닙니다."));
    }

    public User createDummyUser(String userAsset, String email, RedBlueType type) {
        User user = User.builder()
                .userAsset(userAsset)
                .email(email)
                .userType(type)
                .build();
        this.userRepository.save(user);
        return user;
    }
}
