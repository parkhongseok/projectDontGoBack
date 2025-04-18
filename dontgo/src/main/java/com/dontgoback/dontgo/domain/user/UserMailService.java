//package com.dontgoback.dontgo.domain.user;
//
//import com.dontgoback.dontgo.config.jwt.TokenProvider;
//import com.dontgoback.dontgo.config.mail.MailService;
//import com.dontgoback.dontgo.domain.refreshToken.TokenService;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.TokenPurpose;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//
//@Service
//@RequiredArgsConstructor
//public class UserMailService {
//    final MailService mailService;
//
//    public void sendAccountCloseEmail(User user, String token) {
////        String token = tokenProvider.generatePurposeToken(user, Duration.ofMinutes(5), TokenPurpose.ACCOUNT_CLOSE);
//
//        mailService.sendAccountCloseEmail(user.getEmail(), token);
//    }
//}
