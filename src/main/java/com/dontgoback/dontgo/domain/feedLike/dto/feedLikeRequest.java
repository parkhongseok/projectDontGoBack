package com.dontgoback.dontgo.domain.commentLike.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class feedLikeRequest {
    @NotBlank
    private Long feedId;
}
