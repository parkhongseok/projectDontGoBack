package com.dontgoback.dontgo.domain.comment;

import com.dontgoback.dontgo.domain.comment.dto.CommentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 서브쿼리를 JOIN으로 변환하거나,
    // 댓글과 좋아요를 미리 집계한 결과를 별도의 쿼리로 처리한 뒤 조합하는 방법을 고려
    // 예를 들어, CommentLike와 Comment의 카운트를 미리 계산해 놓은 데이터를 캐싱하거나,
    // 두 개의 COUNT를 별도로 조회하는 방식
    @Query("""
                SELECT
                    c.id AS commentId,
                    c.feed.id AS feedId,
                    u.id AS userId,
                    c.content AS content,
                    c.author AS author,
                    u.role AS userRole,
                    c.commentType AS commentType,
                    (SELECT COUNT(l) FROM CommentLike l WHERE l.comment = c) AS likeCount,
                    (SELECT COUNT(sc) FROM Comment sc WHERE sc.parentComment = c AND sc.deletedAt IS NULL) AS subCommentCount,
                    (EXISTS (SELECT 1 FROM CommentLike l WHERE l.comment = c AND l.user.id = :currentUserId)) AS isLiked,
                    c.createdAt AS createdAt,
                    c.updatedAt AS updatedAt,
                    c.deletedAt AS deletedAt
                FROM Comment c
                JOIN c.user u
                WHERE c.feed.id = :feedId AND (:lastCommentId = 0 OR c.id < :lastCommentId) AND c.deletedAt IS NULL
                ORDER BY c.createdAt DESC
                LIMIT :size
            """)
    List<CommentResponse> findCommentsResponse(@Param("lastCommentId") Long lastCommentId,
                                               @Param("size") int size,
                                               @Param("feedId") Long feedId,
                                               @Param("currentUserId") Long currentUserId);
}
