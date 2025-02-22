package com.dontgoback.dontgo.domain.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FeedsResponse {
    private final List<FeedResponse> feeds;

    public int size(){
        return this.size();
    }
}