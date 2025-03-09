package com.dontgoback.dontgo.domain.user;

import com.dontgoback.dontgo.domain.user.dto.UserResponse;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class ApiV1UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResData<UserResponse> getUserInfo(@PathVariable("userId") Long userId) {
        User user = userService.findById(userId);
        UserResponse data = userService.findUser(user);
        return ResData.of("S-200",
                "유저 정보 조회 성공 [uID : %d]".formatted(data.getUserId()),
                data
        );
    }

    @GetMapping("/me")
    public ResData<UserResponse> getMyInfo(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        UserResponse data = userService.findUser(user);
        return ResData.of("S-200",
                "내 정보 조회 성공 [uID : %d]".formatted(data.getUserId()),
                data
        );
    }


}
