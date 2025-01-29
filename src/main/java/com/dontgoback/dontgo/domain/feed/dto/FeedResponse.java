package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.feed.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponse{
    private Post post;
//    private long userId;
//    private String userName;
//    private String postType;
//    private String beforeTime;
//    private String content;
//    private int likeCount;
//    private int commentCount;
}
