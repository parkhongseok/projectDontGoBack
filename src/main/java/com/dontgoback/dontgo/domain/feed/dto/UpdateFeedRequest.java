package com.dontgoback.dontgo.domain.feed.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeedRequest {
    @NotBlank(message = "내용을 입력하세요.")
//    @Size(min = 1, max = 255)
    private String content;
}
