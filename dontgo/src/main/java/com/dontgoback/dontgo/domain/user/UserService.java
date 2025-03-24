package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.user.dto.UserResponse;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.ProfileVisibility;
import org.springframework.web.server.ResponseStatusException;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import static com.dontgoback.dontgo.global.util.EmailMasking.maskEmail;


@Service
@RequiredArgsConstructor
public class UserService {
    final UserRepository userRepository;

    public UserResponse getUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(maskEmail(user.getEmail())) // 이메일 마스킹 처리 1234******@g*****.com 형식
                .profileVisibility(ProfileVisibility.PUBLIC)
                .userName(user.getUserAsset())
                .userType(user.getUserType())
                .build();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                // 401 Unauthorized
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 실패. 올바른 사용자 정보가 아닙니다."));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                // 401 Unauthorized
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 실패. 올바른 사용자 정보가 아닙니다."));
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
