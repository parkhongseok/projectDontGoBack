package com.dontgoback.dontgo.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;


// 유저에게 보낼 응답에 쿠키를 추가하거나, 삭제하거나, 쿠키자체를 변환
public class CookieUtil {
    // 요청값(이름, 값, 만료 기간)을 바탕으로 쿠키 추가
    public static void addCookie(
                                HttpServletResponse response,
                                String name,
                                String value,
                                int maxAge,
                                boolean httpOnly,
                                boolean secure,
                                String path){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setHttpOnly(httpOnly);  // HttpOnly 설정 (JavaScript 접근 불가)
        cookie.setSecure(secure);      // Secure 설정 (HTTPS에서만 쿠키 전달)
        response.addCookie(cookie);
    }

    // 기존 코드를 위해 오버로딩
    public static void addCookie(HttpServletResponse response,
                                 String name,
                                 String value,
                                 int maxAge,
                                 boolean httpOnly,
                                 boolean secure) {
        addCookie(response, name, value, maxAge, httpOnly, secure, "/"); // 기본 path는 "/"
    }


    public static Optional<Cookie> findCookie(Cookie[] cookies, String name){
        return Optional.ofNullable(cookies)
                .flatMap(cs -> Arrays.stream(cs)
                        .filter(cookie -> name.equals(cookie.getName()))
                        .findFirst());
    }

    // 쿠키의 이름을 입력받아서, 쿠키를 삭제
    /*실제로 쿠키를 삭제하는 방법은 없으므로,
    파라미터로 넘어온 키의 쿠키를 비어있는 값으로 바꾸고,
    만료 시간을 0으로 설정하여, 쿠키가 재생성되자마자 만료 처리*/
    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String name, String path,
                                    boolean secure){
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> optionalCookie = findCookie(cookies, name);
        if (optionalCookie.isPresent()) {
            Cookie cookie = optionalCookie.get();

            cookie.setValue("");
            cookie.setPath(path);
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            cookie.setSecure(secure);
            response.addCookie(cookie);
        }
        // 무효한 쿠키를 발급하는 방식으로, 왜냐면 어차피 쿠키는 브라우저에 저장되니까?!
        // 그럼 만약 브라우저에서 재발급을 거부할 수 있다면?! 삭제 실패가 가능할까?
    }

    // 객체 -> 문자열 : 객체를 직렬화하여 쿠키의 값으로 변환
    public static String serialize(Object obj){
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    // 문자열 -> 객체 : 쿠키를 역직렬화하여 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls){
        return cls.cast(SerializationUtils.deserialize(
                Base64.getUrlDecoder().decode(cookie.getValue())
        ));
    }
}
