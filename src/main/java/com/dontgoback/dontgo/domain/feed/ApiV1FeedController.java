package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.feed.dto.*;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class ApiV1FeedController {
    private final FeedService feedService;
    private final UserService userService;

    // MainPage
    @GetMapping("")
    public ResData<FeedsResponse> getFeeds(
            @RequestParam(name = "lastFeedId", required = false, defaultValue = "0") Long lastFeedId,   // 기본값 0
            @RequestParam(name = "size", required = false, defaultValue = "10") int size  // 기본값 10
    ) {

        FeedsResponse data = this.feedService.getFeedsResponse(lastFeedId, size);
        if (lastFeedId == 0)
            return ResData.of("S-200", "메인 피드 조회 성공", data);
        else
            return ResData.of("S-200", "[from %d] 피드 추가 조회 성공".formatted(lastFeedId), data);

    }

    @GetMapping("/{id}")
    public ResData<FeedResponse> getFeed(@PathVariable("id") Long id) {
        return this.feedService
                .getFeedResponse(id)
                .map(feedResponse ->
                        ResData.of(
                                "S-200",
                                "[id : %d] 피드 조회 성공".formatted(id),
                                feedResponse
                        ))
                .orElseGet(() ->
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
//        if (resData.isSuccess()) return ResponseEntity.status(HttpStatus.CREATED).body(resData);

        return ResData.of(
                "F-500",
                "게시물 생성 실패",
                new CreateFeedResponse()
        );
    }

    // 수정
    @PatchMapping("/{id}")
    public ResData<UpdateFeedResponse> update(@RequestBody UpdateFeedRequest updateFeedRequest, @PathVariable("id") Long id) {
        return feedService.updateFeed(id, updateFeedRequest);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResData<DeleteFeedResponse> remove(@PathVariable("id") Long id) {
        return feedService.deleteById(id);
    }

}