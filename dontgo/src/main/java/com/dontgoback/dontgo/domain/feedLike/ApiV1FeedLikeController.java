package com.dontgoback.dontgo.domain.feedLike;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.feedLike.dto.FeedLikeResponse;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedLikes")
public class ApiV1FeedLikeController {
    private final FeedLikeService feedLikeService;
    private final UserService userService;
    private final FeedService feedService;

    // 좋아요 토글
    @GetMapping("/{feedId}")
    public ResponseEntity<ResData<FeedLikeResponse>> toggleFeedLike(
            @PathVariable("feedId") Long feedId,
            @AuthenticationPrincipal User me
    ){
//        User user = userService.findByEmail(principal.getName()); //실패 시 에러 반환
        Feed feed = feedService.findById(feedId);                 // 실패 시 에러 반환

        FeedLikeResponse data = feedLikeService.toggleFeedLike(me.getId(), feedId);
        if (data.isLiked()) {
            ResData<FeedLikeResponse> resData = ResData.of(
                    "S-like",
                    "[fID %d] 게시물에 좋아요를 눌렀습니다.".formatted(feedId),
                    data);
        return ResponseEntity.status(HttpStatus.CREATED).body(resData);
        }else{
            ResData<FeedLikeResponse> resData = ResData.of(
                    "S-disLike",
                    "[fID %d] 게시물에 좋아요를 취소했습니다.".formatted(feedId),
                    data);
            return ResponseEntity.status(HttpStatus.OK).body(resData);
        }
    }


}
