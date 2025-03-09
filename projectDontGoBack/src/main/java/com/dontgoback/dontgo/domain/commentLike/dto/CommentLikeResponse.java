package com.dontgoback.dontgo.domain.commentLike.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentLikeResponse {
    @JsonProperty("isLiked") // 이름을 명시하지 않으면 Jackson은 isXXX 형식의 필드를 XXX로 변환
    private boolean isLiked;
    private int likeCount;
}
