package com.dontgoback.dontgo.domain.feed.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeedRequest {
    @NotBlank(message = "내용을 입력하세요.")
//    @Size(min = 1, max = 255) 문자열 길이 제한 추가할 예정 아마도 ?
    private String content;
}
