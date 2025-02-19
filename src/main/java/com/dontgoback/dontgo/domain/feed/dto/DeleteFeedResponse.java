package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class DeleteFeedResponse {
    private  Long feedId;
    private String content;
    private RedBlueType feedType;
}
