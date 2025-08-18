package com.dontgoback.dontgo.domain.user.dto;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.ProfileVisibility;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.UserRole;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String email;
    private ProfileVisibility profileVisibility;
    private String userName;
    private RedBlueType userType;
    private UserRole userRole;
}
