package com.dontgoback.dontgo.domain.commentLike;

import com.dontgoback.dontgo.domain.comment.Comment;
import com.dontgoback.dontgo.domain.comment.CommentService;
import com.dontgoback.dontgo.domain.commentLike.dto.CommentLikeResponse;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.UserService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/commentLikes")
public class ApiV1CommentLikeController {
    private final UserService userService;
    private final CommentService commentService;
    private final  CommentLikeService commentLikeService;

    // 좋아요 토글
    @GetMapping("/{commentId}")
    public ResponseEntity<ResData<CommentLikeResponse>> toggleCommentLike(@PathVariable("commentId") Long commentId, Principal principal){
        User user = userService.findByEmail(principal.getName()); // 실패 시 에러 반환
        Comment comment = commentService.findById(commentId); // 실패 시 에러 반환
        // 추후 삭제된 게시물에 좋아요 시, 단순 아무런 반응 없도록, 응답 코드 교체? 혹은 삭제된 게시물에 좋아요를 취소하고 싶을수도..?
//        if (commentService.isCommentDeleted(comment)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        CommentLikeResponse data = commentLikeService.toggleCommentLike(user.getId(), commentId);
        if (data.isLiked()){
            ResData<CommentLikeResponse> resData = ResData.of(
                    "S-like",
                    "[cID %d] 댓글에 좋아요를 눌렀습니다.".formatted(commentId),
                    data);
            return ResponseEntity.status(HttpStatus.CREATED).body(resData);
        }else{
            ResData<CommentLikeResponse> resData = ResData.of(
                    "S-disLike",
                    "[cID %d] 댓글에 좋아요를 취소했습니다.".formatted(commentId),
                    data);
            return ResponseEntity.status(HttpStatus.OK).body(resData);
        }

    }

}
