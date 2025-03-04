package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DeleteFeedResponse{
    private Long feedId;
}
