package com.dontgoback.dontgo.domain.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()-> new IllegalArgumentException(("RefreshToken can not found")));
    }

    public void deleteByToken(String refreshToken){
        RefreshToken refreshTokenEntity = findByRefreshToken(refreshToken);
        refreshTokenRepository.delete(refreshTokenEntity);
    }
}

