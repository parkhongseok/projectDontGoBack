package com.dontgoback.dontgo.domain.comment;

import com.dontgoback.dontgo.domain.comment.dto.CommentsResponse;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class ApiV1CommentController {
    private final CommentService commentService;
    // 모든 댓글 조회
    @GetMapping("/{feedId}")
    public ResData<CommentsResponse> getComments(@PathVariable("feedId") Long feedId){
        CommentsResponse commentsResponse = commentService.getCommentsResponse(feedId);
        return ResData.of("S-200",
                "댓글 조회 성공 [fID : %d]".formatted(feedId),
                commentsResponse);
    }

//    @PostMapping("")
//    public
}
