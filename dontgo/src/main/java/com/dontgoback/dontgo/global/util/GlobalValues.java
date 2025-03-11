package com.dontgoback.dontgo.global.util;

import lombok.Getter;

@Getter
public class GlobalValues {
    public static final int MAX_TEXT_LENGTH = 600;
    // 단순히 static이면, 런타임에 참조되고, final까지 붙이면, 컴파일 시 참조가 되서,
    // JPA의 속성에서도 사용 가능
    public static final String ACCESS_TOKEN_NAME = "access_token";
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    // dev 환경
    // public static final String FRONTEND_URL = "http://localhost:3000";
    // public static final String BACKEND_API_URL = "http://localhost:8090/api";

    // prod 환경
   public static final String FRONTEND_URL = "https://dontgoback.kro.kr";
   public static final String BACKEND_API_URL = "https://dontgoback.kro.kr/api";






}
