package com.dontgoback.dontgo.domain.comment;

import com.dontgoback.dontgo.domain.comment.dto.*;
import com.dontgoback.dontgo.domain.feed.Feed;


import com.dontgoback.dontgo.domain.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    // 댓글 작성자 본인인 지 확인
    private static void authorizeCommentUser(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getUser().getUsername().equals(userName)) {
            // 403 Forbidden
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한 없음 : 본인이 작성한 게시물이 아닙니다.");
        }
    }

    // 다건 조회
    public CommentsResponse getCommentsResponse(Long lastCommentId, int size, Long feedId, Long currentUserId) {
        return new CommentsResponse(commentRepository.findCommentsResponse(lastCommentId, size, feedId, currentUserId));
    }

    // 댓글 등록
    @Transactional
    public CreateCommentResponse createComment(Feed feed, User user, @Valid CreateCommentRequest createCommentRequest) {

        Comment comment;
        try {
            comment = Comment.builder()
                    .user(user)
                    .feed(feed)
                    .content(createCommentRequest.getContent())
                    .author(user.getUserAsset())
                    .commentType(user.getUserType())
                    .parentComment(null) // 최상위 댓글
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[답글 등록 실패] 유저 정보 부족");
        }

        saveComment(comment);

        return CreateCommentResponse.builder()
                .commentId(comment.getId())
                .feedId(feed.getId())
                .userId(user.getId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .commentType(comment.getCommentType())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // 답글 수정
    @Transactional
    public UpdateCommentResponse updateComment(Long id, UpdateCommentRequest updateCommentRequest) {
        Comment comment = findById(id); // 실패 시  404 NOT_FOUND 반환
        authorizeCommentUser(comment);  // 해당 답글을 지울 자격이 있는지 검사
        comment.setContent(updateCommentRequest.getContent());

        comment.preUpdate(); // 수동으로 updateAt 시간 갱신

        return UpdateCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // 답글 삭제
    @Transactional
    public DeleteCommentResponse deleteById(Long id) {
        Comment comment = findById(id); // 답글이 있는지 검사
        authorizeCommentUser(comment);  // 해당 답글을 지울 자격이 있는지 검사
        if (comment.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 삭제된 게시물입니다.");
        }
        comment.setDeletedAt(LocalDateTime.now());
        return new DeleteCommentResponse(comment.getId());
    }

    public Comment createDummyComment(Feed feed, User user, String content) {
        Comment comment = Comment.builder()
                .feed(feed)
                .user(user)
                .author(user.getUserAsset())
                .commentType(user.getUserType())
                .content(content)
                .build();
        commentRepository.save(comment);
        return comment;
    }

    // 저장
    public void saveComment(Comment comment) {
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "답글 저장 실패 : 잘못된 요청 데이터입니다.");
        }
    }
    // 조회
    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "%d번 답글을 찾을 수 없습니다.".formatted(id)));
    }

    // 삭제 여부 검사
    public boolean isCommentDeleted(Comment comment){
        return comment.getDeletedAt() != null;
    }


}
