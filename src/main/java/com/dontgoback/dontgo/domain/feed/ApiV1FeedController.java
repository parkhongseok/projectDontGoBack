package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.feed.dto.*;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class ApiV1FeedController {
    private final FeedService feedService;
    private final UserService userService;

    // MainPage
    @GetMapping("")
    public ResData<FeedsResponse> getFeeds(){
        FeedsResponse data = this.feedService.getFeedsResponse();
        return ResData.of("S-200", "메인 피드 조회 성공", data);
    }

    @GetMapping("/{id}")
    public ResData<FeedResponse> getFeed(@PathVariable("id") Long id){
        return this.feedService
                .getFeedResponse(id)
                .map(feedResponse ->
                        ResData.of(
                                "S-200",
                                "피드 조회 성공",
                                feedResponse
                        ))
                .orElseGet(()->
                        ResData.of(
                            "F-400",
                            "피드 조회 실패",
                            null
                ));
    }

    // 생성
    @PostMapping("")
    public ResData<CreateFeedResponse> createFeed(@RequestBody CreateFeedRequest feedRequest, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        ResData<CreateFeedResponse> resData = this.feedService.createFeed(user, feedRequest);
        if (resData.isSuccess()) return resData;

        return ResData.of(
                resData.getResultCode(),
                resData.getMessage(),
                new CreateFeedResponse()
        );
    }

    // 수정
    @PatchMapping("/{id}")
    public ResData<UpdateFeedResponse> update(@RequestBody UpdateFeedRequest updateFeedRequest, @PathVariable("id") Long id){
        return feedService.updateFeed(id, updateFeedRequest);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResData<DeleteFeedResponse> remove (@PathVariable("id") Long id) {
        return feedService.deleteById(id);
    }

}