package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class CreateFeedResponse {
    private Long feedId;
    private Long userId;
    private String content;
    private String userName;
    private RedBlueType feedType;
    private int likeCount = 0;
    private int commentCount = 0;
    private LocalDateTime createdAt;
}
