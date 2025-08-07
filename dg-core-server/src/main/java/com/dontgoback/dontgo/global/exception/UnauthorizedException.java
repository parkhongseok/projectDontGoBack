package com.dontgoback.dontgo.global.exception;

/**
 * JWT 인증 실패 (401 Unauthorized 등)에 대응하기 위한 커스텀 예외
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
