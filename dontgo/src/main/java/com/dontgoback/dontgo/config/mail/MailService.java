package com.dontgoback.dontgo.config.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
    // properties값으로 초기화
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String HostEmail;
    @Value("${app.FRONTEND_URL}")
    private String FRONTEND_URL;

    public void sendAccountCloseEmail(String email, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // true/false 첨부파일/HTML/텍스트 등을 섞어서 보낼 수 있는 복합 MIME 구조
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setTo(email); // 수신자 이메일 주소
            helper.setSubject("[DontGoBack] 회원 탈퇴 요청"); // 이메일 제목

            // HTML 템플릿에 전달할 값 구성
            Context context = new Context(); // Thymeleaf Context 객체 생성
            // 링크
            String closeAccountLink = "%s/users/me/?token=%s".formatted(FRONTEND_URL, token); // 링크 구성
            context.setVariable("CloseAccountLink", closeAccountLink); // HTML 템플릿에 변수 바인딩
            // 요청 만료 시간
            ZonedDateTime expiryTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusMinutes(5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH:mm");
            String formattedExpiry = expiryTime.format(formatter); // 예: "2025년 4월 15일 17:43"
            context.setVariable("expiryTime", formattedExpiry);

            // 템플릿 파일명: withdraw-email.html (resources/templates/에 존재해야 함)
            String html = templateEngine.process("close-account-email", context); // HTML 템플릿 렌더링

            helper.setText(html, true); // HTML 형식으로 이메일 본문 설정

            javaMailSender.send(message); // 이메일 전송

            log.info("Succeeded to send Email");
        } catch (MessagingException e) {
            log.info("Failed to send Email");
            throw new RuntimeException("메일 전송 실패", e); // 예외 처리
        }
    }
}
