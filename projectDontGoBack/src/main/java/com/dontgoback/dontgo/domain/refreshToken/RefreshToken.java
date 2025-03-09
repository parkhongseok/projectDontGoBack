package com.dontgoback.dontgo.domain.refreshToken;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
엔티티 생성 -> 레파지토리에 메서드 선언 -> 토큰 필터 구현(config)
-> 유저 서비스 추가하기(UserService.findById)
-> 리프레시 토큰 서비스 추가(RefreshTokenService.findByRefreshToken)
-> 토큰 서비스 추가(TokenService.createNewAccessToken)
-> 토큰 컨트롤러 추가
* */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken){
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken){
        this.refreshToken = newRefreshToken;
        return this;
    }

}
