package com.dontgoback.dontgo.domain.refreshToken;

import com.dontgoback.dontgo.domain.refreshToken.dto.CreateAccessTokenRequest;
import com.dontgoback.dontgo.domain.refreshToken.dto.CreateAccessTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    // Post 요청이 오면, 리프레시 토큰을 기반으로 새로운 액세스 토큰을 생성해서 반환해줌.
    @GetMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken
            (@CookieValue(name = "refresh_token", required = false) String refreshToken){
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String newAccessToken = tokenService.createNewAccessToken(refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }
}