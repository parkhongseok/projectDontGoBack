package com.dontgoback.dontgo.domain.feed.controller;

import com.dontgoback.dontgo.domain.feed.dto.FeedResponse;
import com.dontgoback.dontgo.domain.feed.dto.FeedsResponse;
import com.dontgoback.dontgo.domain.feed.entity.Feed;
import com.dontgoback.dontgo.domain.feed.service.FeedService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class ApiV1FeedController {
    private final FeedService feedService;

    @GetMapping("")
    public ResData<FeedsResponse> getFeeds(){
        List<Feed> feeds = this.feedService.getFeeds();
        return ResData.of("S-200", "성공", new FeedsResponse(feeds));
    }

    @GetMapping("/{id}")
    public ResData<FeedResponse> getFeed(@PathVariable("id") Long id){
        return feedService
                .getFeed(id)
                .map(feed ->
                        ResData.of(
                                "S-200",
                                "성공",
                                new FeedResponse(feed)
                        ))
                .orElseGet(()->
                        ResData.of(
                            "S-200",
                            "실패",
                            null
                ));
    }

//    @PostMapping("")
//    public
}
