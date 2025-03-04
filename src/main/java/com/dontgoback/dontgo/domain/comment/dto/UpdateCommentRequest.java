package com.dontgoback.dontgo.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.dontgoback.dontgo.global.util.GlobalValues.MAX_TEXT_LENGTH;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCommentRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    @Size(min = 1, max = MAX_TEXT_LENGTH)
    private String content;
}
