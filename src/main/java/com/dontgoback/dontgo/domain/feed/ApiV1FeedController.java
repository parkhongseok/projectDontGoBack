package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.feed.dto.*;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class ApiV1FeedController {
    private final FeedService feedService;
    private final UserService userService;

    @GetMapping("")
    public ResData<FeedsResponse> getFeeds(){
        FeedsResponse data = this.feedService.getFeedsResponse();
        return ResData.of("S-200", "성공", data);
    }

    @GetMapping("/{id}")
    public ResData<FeedResponse> getFeed(@PathVariable("id") Long id){
        return this.feedService
                .getFeedResponse(id)
                .map(feedResponse ->
                        ResData.of(
                                "S-200",
                                "성공",
                                feedResponse
                        ))
                .orElseGet(()->
                        ResData.of(
                            "S-400",
                            "실패",
                            null
                ));
    }

    // Create
    @PostMapping("")
    public ResData<CreateFeedResponse> createFeed(@RequestBody FeedRequest feedRequest) {
        // 파라미터 부분에 validation 어노테이션을 사용하여, 비어있는지 미리 검사 등
        // 그리고 응답 객체에 포장해서 return
        User user = userService.findById(1L);
        ResData<Feed> resData = this.feedService.createFeed(user, feedRequest);
        if (resData.isFail()) return (ResData)resData;

        return ResData.of(
                resData.getResultCode(),
                resData.getMessage(),
                new CreateFeedResponse(resData.getData())
        );
    }


    @PatchMapping("/{id}")
    public ResData<Object> update(@RequestBody UpdateFeedRequest updateFeedRequest, @PathVariable("id") Long id){
        Optional<Feed> optionalFeed = this.feedService.findById(id);

        // 게시물 존재 여부 확인
        if (optionalFeed.isEmpty()) return ResData.of(
                "F-1",
                "%d번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        // 회원 권한 체크
        ResData<Feed> resData = this.feedService.updateFeed(optionalFeed.get(), updateFeedRequest);

        return ResData.of(
                resData.getResultCode(),
                resData.getMessage(),
                new UpdateFeedResponse(resData.getData())
        );
    }


    @DeleteMapping("/{id}")
    public ResData<DeleteFeedResponse> remove (@PathVariable("id") Long id) {
        // 바로 삭제 or 찾고 삭제 중에 하나 선택
        Optional<Feed> optionalFeed = feedService.findById(id);

        if (optionalFeed.isEmpty()) return ResData.of(
                "F-1",
                "%d번 게시물은 존재하지 않습니다.".formatted(id),
                null
        );

        ResData<Feed> resData = feedService.deleteById(id);
        return ResData.of(
                resData.getResultCode(),
                resData.getMessage(),
                new DeleteFeedResponse(optionalFeed.get())
        );
    }


}