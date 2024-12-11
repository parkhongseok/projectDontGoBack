package com.dontgoback.dontgo.global.rsData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RsData<T> {
    private String resultCode;
    private String message;
    private T data;

    public static <T> RsData<T> of(String resultCode, String message, T data) {
        return new RsData<>(resultCode, message, data);
    }

    public static <T> RsData<T> of(String resultCode, String message) {
        return of(resultCode, message, null);
    }
    @JsonIgnore
    public boolean isSuccess() {
        return resultCode.startsWith("S-");
    }

    @JsonIgnore
    public boolean isFail() {
        return !isSuccess();
    }
}
