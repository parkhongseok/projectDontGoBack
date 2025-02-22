package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class UpdateFeedResponse {
    private Long feedId;
    private String content;
    private RedBlueType feedType;
}
