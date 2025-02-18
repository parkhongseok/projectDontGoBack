package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.user.dto.AddUserRequest;
import com.dontgoback.dontgo.domain.user.dto.LoginUserResponse;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import com.dontgoback.dontgo.global.resData.ResData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiV1UserController {
    private final UserService userService;

    @PostMapping("/login")
    public String signup(AddUserRequest request) {
        userService.save(request);
        return "";
    }

    @GetMapping("/me")
    public ResData<LoginUserResponse> getUserInfo(Principal principal) {
        User user = userService.findByEmail(principal.getName());

        if (user == null) {
            return ResData.of("S-400",
                    "실패",
                    null) ;
        }

        return ResData.of("S-200",
               "성공",
                LoginUserResponse.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .userName("BackDummy")
                        .userType(RedBlueType.BLUE)
                        .build()
        );
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "redirect:/login";
    }

}
