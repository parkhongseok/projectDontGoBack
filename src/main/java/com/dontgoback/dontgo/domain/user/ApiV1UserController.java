package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.user.dto.LoginUserResponse;
import com.dontgoback.dontgo.global.resData.ResData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiV1UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResData<LoginUserResponse> getUserInfo(Principal principal) {
        LoginUserResponse myData = userService.findMe(principal.getName());
        return ResData.of("S-200",
               "내 정보 조회 성공 [uID : %d]".formatted(myData.getUserId()),
                myData
        );
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

}
