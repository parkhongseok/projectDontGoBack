package com.dontgoback.dontgo.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponseDTO {
    private String username;
    private String postType;
    private String beforeTime;
    private String content;
    private int likeCount;
    private int commentCount;
}
