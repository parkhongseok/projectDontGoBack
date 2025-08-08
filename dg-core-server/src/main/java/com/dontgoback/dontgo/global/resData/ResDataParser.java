package com.dontgoback.dontgo.global.resData;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

public class ResDataParser {
    public static <T> T extract(ResponseEntity<ResData<T>> response) {
        ResData<T> body = response.getBody();

        if (body == null) {
            throw new RuntimeException("응답 본문이 비어 있습니다");
        }

        if (body.isFail()) {
            throw new RuntimeException("실패 응답: " + body.getMessage());
        }

        return body.getData();
    }

    public static <T> ParameterizedTypeReference<ResData<T>> type(Class<T> clazz) {
        return new ParameterizedTypeReference<>() {};
        // 또는 TypeToken 기반으로 구현할 수도 있음 (복잡도 ↑)
    }
}
