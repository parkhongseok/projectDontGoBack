package com.dontgoback.dontgo.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@Builder
public class UpdateCommentResponse{
    private Long commentId;
    //    private Long feedId;
    private String content;
    private LocalDateTime updatedAt;
}
