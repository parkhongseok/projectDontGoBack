package com.dontgoback.dontgo.domain.refreshToken;

import com.dontgoback.dontgo.domain.user.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new IllegalArgumentException(("RefreshToken can not found")));
    }


    @Transactional
    public void deleteByUserId(Long userId){
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    // 생성된 리프레시 토큰을 전달받아서 DB에 저장
    public void saveOrUpdateRefreshToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(user, newRefreshToken));

        refreshTokenRepository.saveAndFlush(refreshToken);
    }
}

