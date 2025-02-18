package com.dontgoback.dontgo.domain.user.dto;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class LoginUserResponse {
    private Long userId;
    private String email;
    private String userName;
    private RedBlueType userType;
}
