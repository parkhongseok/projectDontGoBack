package com.dontgoback.dontgo.domain.comment.dto;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
public class CommentTypeDto {
    private Long commentId;
    private Long feedId;
    private Long userId;
    private String author;
    private String content;
    private RedBlueType commentType;
    private int likeCount;
    private int subCommentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}