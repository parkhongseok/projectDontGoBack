package com.dontgoback.dontgo.domain.comment;

import com.dontgoback.dontgo.domain.comment.dto.*;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feed.FeedService;
import com.dontgoback.dontgo.domain.feed.dto.DeleteFeedResponse;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.resData.ResData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class ApiV1CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final FeedService feedService;

    // 모든 댓글 조회
    @GetMapping("/{feedId}")
    public ResData<CommentsResponse> getComments(@Valid @PathVariable("feedId") Long feedId, @RequestParam(name = "lastCommentId", required = false, defaultValue = "0") Long lastCommentId,   // 기본값 0
                                                 @RequestParam(name = "size", required = false, defaultValue = "10") int size,  // 기본값 10
                                                 @AuthenticationPrincipal User me) {
//        User user = userService.findByEmail(principal.getName());
        CommentsResponse data = commentService.getCommentsResponse(lastCommentId, size, feedId, me.getId());
        if (lastCommentId == 0) return ResData.of("S-200", "댓글 조회 성공 [fID %d]".formatted(feedId), data);
        else {
            if (!data.getComments().isEmpty())
                return ResData.of("S-200", "[cID %d ~] 댓글 추가 조회 성공 ".formatted(lastCommentId), data);
            else return ResData.of("S-200", "불러올 댓글 없음", data);
        }
    }

    // 생성
    @PostMapping("")
    public ResponseEntity<ResData<CreateCommentResponse>> createComment(
            @Valid @RequestBody CreateCommentRequest createCommentRequest, Principal principal) {
        User user = userService.findByEmail(principal.getName()); // 여기서 실패 시 에러 반환
        Feed feed = feedService.findById(createCommentRequest.getFeedId()); // 실패 시 피드 존재하지 않음 에러 반환
        if (feedService.isFeedDeleted(feed))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "답글 생성 실패 : 피드가 존재하지 않습니다.");

        CreateCommentResponse data = commentService.createComment(feed, user, createCommentRequest);
        ResData<CreateCommentResponse> resdata = ResData.of("S-200", "[fID %d - cID %d] 답글이 생성되었습니다.".formatted(data.getFeedId(), data.getCommentId()), data);
        return ResponseEntity.status(HttpStatus.CREATED).body(resdata);
    }

    // 수정
    @PatchMapping("/{id}")
    public ResData<UpdateCommentResponse> update(@Valid @RequestBody UpdateCommentRequest updateCommentRequest, @PathVariable("id") Long id) {
        UpdateCommentResponse data = commentService.updateComment(id, updateCommentRequest);
        return ResData.of("S-feedUpdate", "%d번 답글이 수정되었습니다.".formatted(id), data);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResData<DeleteCommentResponse> remove(@PathVariable("id") Long id) {
        DeleteCommentResponse data = commentService.deleteById(id);
        return ResData.of("S-2", "%d번 답글이 삭제되었습니다.".formatted(id), data);
    }


}
