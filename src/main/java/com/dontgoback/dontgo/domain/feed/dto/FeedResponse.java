package com.dontgoback.dontgo.domain.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
public interface FeedResponse {
    Long getFeedId(); // 컬럼 별칭 "feedId"와 매핑
    Long getUserId(); // 컬럼 별칭 "userId"와 매핑
    String getContent();
    String getUserName();
    String getFeedType();
    int getLikeCount();
    int getCommentCount();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getDeletedAt();
}