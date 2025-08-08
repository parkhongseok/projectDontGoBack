package com.dontgoback.dontgo.interserver.auth.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InterServerJwtRequest {
    private final String clientId;
    private final String clientSecret;
}
