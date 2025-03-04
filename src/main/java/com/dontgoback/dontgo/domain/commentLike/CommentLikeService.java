package com.dontgoback.dontgo.domain.commentLike;

import com.dontgoback.dontgo.domain.commentLike.dto.CommentLikeResponse;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.CommentLikeId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeService {
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public CommentLikeResponse toggleCommentLike(Long userId, Long commentId){
        CommentLikeId id = new CommentLikeId(userId, commentId);
        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findById(id);
        if (optionalCommentLike.isPresent()){
            //좋아요 이미 누른 경우 -> 취소
            CommentLike commentLike = optionalCommentLike.get();
            commentLikeRepository.delete(commentLike);
            return new CommentLikeResponse(false, CountByCommentId(commentId));
        }else{
            // 좋아유
            CommentLike commentLike = new CommentLike(id);
            commentLikeRepository.save(commentLike);
            return new CommentLikeResponse(true, CountByCommentId(commentId));
        }
    }

    public int CountByCommentId(Long commentId){
        return commentLikeRepository.countByCommentId(commentId);
    }



}
