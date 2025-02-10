package com.dontgoback.dontgo.domain.feed.dto;

import com.dontgoback.dontgo.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedCreateRequest {
    private User user;
    private String content;
}
