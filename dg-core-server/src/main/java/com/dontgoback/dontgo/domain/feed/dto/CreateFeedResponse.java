package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
public class CreateFeedResponse {
    private Long feedId;
    private Long userId;
    private String content;
    private String author;
    private UserRole userRole;
    private RedBlueType feedType;
    private int likeCount;
    private int commentCount;
    private int isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
