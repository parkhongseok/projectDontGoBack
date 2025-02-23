package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class DeleteFeedResponse {
    private Long feedId;
    private Long userId = 0L;
    private String content = "null";
    private String userName = "null";
    private RedBlueType feedType;
    private int likeCount = 0;
    private int commentCount = 0;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
