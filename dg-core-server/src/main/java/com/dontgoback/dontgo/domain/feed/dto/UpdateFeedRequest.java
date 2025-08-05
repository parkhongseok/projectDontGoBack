package com.dontgoback.dontgo.domain.feed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.dontgoback.dontgo.global.util.GlobalValues.MAX_TEXT_LENGTH;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeedRequest {
    @NotBlank(message = "내용을 입력하세요.")
    @Size(min = 1, max = MAX_TEXT_LENGTH)
    private String content;
}
