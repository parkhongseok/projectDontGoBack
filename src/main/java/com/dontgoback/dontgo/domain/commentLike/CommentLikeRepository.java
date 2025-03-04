package com.dontgoback.dontgo.domain.commentLike;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    int countByCommentId(Long commentId);
}
