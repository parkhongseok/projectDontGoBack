package com.dontgoback.dontgo.global.util;

public class EmailMasking {
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        // 사용자 이름 일부만 보이고 나머지는 * 처리
        String maskedUsername = username.substring(0, Math.min(4, username.length())) + "******";

        // 도메인 일부만 보이도록 처리
        String[] domainParts = domain.split("\\.");
        String maskedDomain = domainParts[0].charAt(0) + "*****";

        return maskedUsername + "@" + maskedDomain + (domainParts.length > 1 ? "." + domainParts[1] : "");
    }

}
