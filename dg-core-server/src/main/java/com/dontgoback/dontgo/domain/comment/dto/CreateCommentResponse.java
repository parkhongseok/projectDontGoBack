package com.dontgoback.dontgo.domain.comment.dto;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
@Builder
@AllArgsConstructor
public class CreateCommentResponse{
    private Long commentId;
    private Long feedId;
    private Long userId;
    private String author;
    private UserRole userRole;
    private String content;
    private RedBlueType commentType;
    private int likeCount;
    private int subCommentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
