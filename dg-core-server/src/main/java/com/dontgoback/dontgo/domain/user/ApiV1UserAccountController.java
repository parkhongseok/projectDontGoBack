package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.config.jwt.TokenProvider;
import com.dontgoback.dontgo.config.mail.MailService;
import com.dontgoback.dontgo.domain.accountStateHistory.AccountStateService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.TokenPurpose;
import com.dontgoback.dontgo.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.dontgoback.dontgo.global.util.GlobalValues.REFRESH_TOKEN_API_PATH;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiV1UserAccountController {
    private final UserService userService;
    private final MailService mailService;
    private final TokenProvider tokenProvider;
    private final AccountStateService accountStateService;

    @Value("${cookie.secure}")
    private boolean secureCookie;

    @Value("${app.FRONTEND_URL}")
    public String FRONT_URL;

    @PostMapping("/account-close/email-request")
    public ResponseEntity<?> sendAccountCloseRequestEmail(@AuthenticationPrincipal User me){
        // 영속 상태의 JPA Entity인지 검증
        User user = userService.findByEmail(me.getEmail());

        // 계정 삭제 목적 특수 토큰 발급
        String token = tokenProvider.generatePurposeToken(user, Duration.ofMinutes(5), TokenPurpose.ACCOUNT_CLOSE);

        // 메일 발송
        mailService.sendAccountCloseEmail(user.getEmail(), token);

        return ResponseEntity.ok("이메일을 확인해주세요");
    }

    // email링크를 클릭하며 필요한 작업 따라서 필터에서 열어야함, 단, GET 메서드만
    @GetMapping("/account-close")
    public ResponseEntity<?> doCloseAccount(@RequestParam("token") String token,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        // 1. 유효하지 않은 토큰인지 확인
        if (!tokenProvider.isPurposeTokenValid(token, TokenPurpose.ACCOUNT_CLOSE)) {
            URI redirect = URI.create(FRONT_URL + "/login?status=close-fail");
            return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
        }

        // 2. 유저 식별 (from token)
        Long userId = tokenProvider.getUserId(token);
        User user = userService.findById(userId);

        // 3. 리프레시 토큰 삭제
        // 4. 계정 상태 "탈퇴 요청됨" 등으로 변경
        accountStateService.closeAccount(user);

        // 클라이언트 쿠키 무효화 (중요!)
        CookieUtil.deleteCookie(request, response, "refresh_token", REFRESH_TOKEN_API_PATH ,secureCookie);

        // 5. 리다이렉트 (성공 or 실패)
        LocalDate untilDate = LocalDate.now().plusDays(14);
        String formattedDate = untilDate.format(DateTimeFormatter.ISO_DATE); // "YYYY-MM-DD"
        URI redirect = URI.create(FRONT_URL + "/login?status=close-success&until=" + formattedDate);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
    }


    @PostMapping("/account-inactive/email-request")
    public ResponseEntity<?> sendAccountInactiveRequestEmail(@AuthenticationPrincipal User me){
        // 영속 상태의 JPA Entity인지 검증
        User user = userService.findByEmail(me.getEmail());

        // 계정 비활성화 목적 특수 토큰 발급
        String token = tokenProvider.generatePurposeToken(user, Duration.ofMinutes(5), TokenPurpose.ACCOUNT_INACTIVE);

        // 메일 발송
        mailService.sendAccountInactiveEmail(user.getEmail(), token);

        return ResponseEntity.ok("이메일을 확인해주세요");
    }

    // email링크를 클릭하며 필요한 작업 따라서 필터에서 열어야함, 단, GET 메서드만
    @GetMapping("/account-inactive")
    public ResponseEntity<?> DeactivateAccount(@RequestParam("token") String token,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        // 1. 유효하지 않은 토큰인지 확인
        if (!tokenProvider.isPurposeTokenValid(token, TokenPurpose.ACCOUNT_INACTIVE)) {
            URI redirect = URI.create(FRONT_URL + "/login?status=inactive-fail");
            return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
        }

        // 2. 유저 식별 (from token)
        Long userId = tokenProvider.getUserId(token);
        User user = userService.findById(userId);

        // 3. 리프레시 토큰 삭제
        // 4. 계정 상태 "탈퇴 요청됨" 등으로 변경
        accountStateService.deactivateAccount(user);

        // 클라이언트 쿠키 무효화
        CookieUtil.deleteCookie(request, response, "refresh_token", REFRESH_TOKEN_API_PATH, secureCookie);

        // 5. 리다이렉트 (성공 or 실패)
        LocalDate untilDate = LocalDate.now().plusDays(14);
        String formattedDate = untilDate.format(DateTimeFormatter.ISO_DATE); // "YYYY-MM-DD"
        URI redirect = URI.create(FRONT_URL + "/login?status=inactive-success&until=" + formattedDate);
        return ResponseEntity.status(HttpStatus.FOUND).location(redirect).build();
    }
}