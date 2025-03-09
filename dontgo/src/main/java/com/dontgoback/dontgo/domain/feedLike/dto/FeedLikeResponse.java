package com.dontgoback.dontgo.domain.feedLike.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class FeedLikeResponse {
    @JsonProperty("isLiked") // 이름을 명시하지 않으면 Jackson은 isXXX 형식의 필드를 XXX로 변환
    private boolean isLiked;

    private int likeCount;
}
