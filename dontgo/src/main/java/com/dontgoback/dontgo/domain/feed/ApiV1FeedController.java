package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.feed.dto.*;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.resData.ResData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static org.springframework.http.ResponseEntity.status;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feeds")
public class ApiV1FeedController {
    private final FeedService feedService;
    private final UserService userService;

    // 다건 조회 : 페이징 처리된 메인 피드
    @GetMapping("")
    public ResData<FeedsResponse> getFeeds(
            @RequestParam(name = "lastFeedId", required = false, defaultValue = "0") Long lastFeedId,   // 기본값 0
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,  // 기본값 10
            // @AuthenticationPrincipal은 SecurityContextHolder.getContext().getAuthentication().getPrincipal()을 자동으로 꺼내서 넣어주는 역할
            @AuthenticationPrincipal User me
    ) {
//        User me = userService.findByEmail(principal.getName()); // 없으면 에러 반환
        FeedsResponse data = feedService.getFeedsResponse(lastFeedId, size, me.getId());
        if (lastFeedId == 0)
            return ResData.of("S-200", "메인 피드 조회 성공", data);
        else {
            if (!data.getFeeds().isEmpty())
                return ResData.of("S-200", "[fID %d ~] 메인 피드 추가 조회 성공".formatted(lastFeedId), data);
            else
                return ResData.of("S-200", "불러올 메인 피드 없음", data);
        }
    }

    // 프로필 피드 다건 조회 : 페이징 처리된 메인 피드 + 내정보 / 유저 정보
    @GetMapping("/profile")
    public ResData<FeedsResponse> getProfileFeeds(
            @RequestParam(name = "userId", required = true) Long userId,
            @RequestParam(name = "lastFeedId", required = false, defaultValue = "0") Long lastFeedId,   // 기본값 0
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,  // 기본값 10
            @AuthenticationPrincipal User me
    ) {
//        User me = userService.findByEmail(principal.getName()); // 내정보 -> 좋아요 누른적 있는지 조회
        User targetUser = userService.findById(userId); // 타겟 유저

        FeedsResponse data = feedService.getProfileFeedsResponse(targetUser.getId(), lastFeedId, size, me.getId());
        if (lastFeedId == 0)
            return ResData.of("S-200", "프로필 피드 조회 성공", data);
        else {
            if (!data.getFeeds().isEmpty())
                return ResData.of("S-200", "[fID %d ~] 프로필 피드 추가 조회 성공".formatted(lastFeedId), data);
            else
                return ResData.of("S-200", "불러올 프로필 피드 없음", data);
        }
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResData<FeedResponse> getFeed(@PathVariable("id") Long id, @AuthenticationPrincipal User me) {
//        User user = userService.findByEmail(principal.getName());

        FeedResponse data = feedService.getFeedResponse(id, me.getId());
        return ResData.of(
                "S-200",
                "[fID : %d] 피드 조회 성공".formatted(id),
                data
        );
        // 예외는 서비스에서 던짐, 예외 객체에 body 따로 없음, 필요 시 try catch 가능
    }

    // 생성
    @PostMapping("")
    public ResponseEntity<ResData<CreateFeedResponse>> createFeed(
            @Valid @RequestBody CreateFeedRequest feedRequest,
            // 아래 부분에선 필요한 type 정보 등이 토큰으로부터 인증객체에 주입되는데, 매일 변동되니까,
            // 자정 전후의 경계조건에서 오류 생갈 여지 큼
            // 따라서 id나 email기준으로 실시간 조회하는 수밖에 없음
            Principal principal
            ) {
        User me = userService.findByEmail(principal.getName()); // 여기서 실패 시 에러 반환
        CreateFeedResponse data = feedService.createFeed(me, feedRequest);
        ResData<CreateFeedResponse> resdata = ResData.of(
                "S-200",
                "[fID %d] 게시물이 생성되었습니다.".formatted(data.getFeedId()),
                data);
        return ResponseEntity.status(HttpStatus.CREATED).body(resdata);
    }

    // 수정
    @PatchMapping("/{id}")
    public ResData<UpdateFeedResponse> update(@Valid @RequestBody UpdateFeedRequest updateFeedRequest, @PathVariable("id") Long id) {
        UpdateFeedResponse data = feedService.updateFeed(id, updateFeedRequest);
        return ResData.of(
                "S-feedUpdate",
                "%d번 게시글이 수정되었습니다.".formatted(id),
                data
        );
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResData<DeleteFeedResponse> remove(@PathVariable("id") Long id) {
        DeleteFeedResponse data = feedService.softDeleteById(id);
        return ResData.of(
                "S-2",
                "%d번 게시글이 삭제되었습니다.".formatted(id),
                data
        );
    }

}