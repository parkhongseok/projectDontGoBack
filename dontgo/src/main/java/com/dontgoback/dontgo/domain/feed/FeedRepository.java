package com.dontgoback.dontgo.domain.feed;
import com.dontgoback.dontgo.domain.feed.dto.FeedResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query("""
    SELECT
        f.id AS feedId,
        u.id AS userId,
        f.content AS content,
        f.author AS author,
        f.feedType AS feedType,
        (SELECT COUNT(l) FROM FeedLike l WHERE l.feed = f) AS likeCount,
        (SELECT COUNT(c) FROM Comment c WHERE c.feed = f AND c.deletedAt IS NULL) AS commentCount,
        (EXISTS (SELECT 1 FROM FeedLike l WHERE l.feed = f AND l.user.id = :currentUserId)) AS isLiked,
        f.createdAt AS createdAt,
        f.updatedAt AS updatedAt,
        f.deletedAt AS deletedAt
    FROM Feed f
    JOIN f.user u
    WHERE (:lastFeedId = 0 OR f.id < :lastFeedId) AND f.deletedAt IS NULL
    ORDER BY f.createdAt DESC
    LIMIT :size
    """)
    List<FeedResponse> findFeedsResponse(@Param("lastFeedId") Long lastFeedId,
                                         @Param("size") int size,
                                         @Param("currentUserId") Long currentUserId);

   @Query("""
    SELECT
        f.id AS feedId,
        u.id AS userId,
        f.content AS content,
        f.author AS author,
        f.feedType AS feedType,
        (SELECT COUNT(l) FROM FeedLike l WHERE l.feed = f) AS likeCount,
        (SELECT COUNT(c) FROM Comment c WHERE c.feed = f AND c.deletedAt IS NULL) AS commentCount,
        (EXISTS (SELECT 1 FROM FeedLike l WHERE l.feed = f AND l.user.id = :currentUserId)) AS isLiked,
        f.createdAt AS createdAt,
        f.updatedAt AS updatedAt,
        f.deletedAt AS deletedAt
    FROM Feed f
    JOIN f.user u
    WHERE (u.id = :targetUserId) AND (:lastFeedId = 0 OR f.id < :lastFeedId) AND (f.deletedAt IS NULL) 
    ORDER BY f.createdAt DESC
    LIMIT :size
    """)
    List<FeedResponse> findProfileFeedsResponse(
                                        @Param("targetUserId") Long targetUserId,
                                        @Param("lastFeedId") Long lastFeedId,
                                         @Param("size") int size,
                                         @Param("currentUserId") Long currentUserId);

    @Query("""
    SELECT
        f.id AS feedId,
        u.id AS userId,
        f.content AS content,
        f.author AS author,
        f.feedType AS feedType,
        (SELECT COUNT(l) FROM FeedLike l WHERE l.feed = f) AS likeCount,
        (SELECT COUNT(c) FROM Comment c WHERE c.feed = f AND c.deletedAt IS NULL) AS commentCount,
        (EXISTS (SELECT 1 FROM FeedLike l WHERE l.feed = f AND l.user.id = :currentUserId)) AS isLiked,
        f.createdAt AS createdAt,
        f.updatedAt AS updatedAt,
        f.deletedAt AS deletedAt
    FROM Feed f
    JOIN f.user u
    WHERE f.id = :myFeedId AND f.deletedAt IS NULL
    """)
    Optional<FeedResponse> findFeedResponseById(@Param("myFeedId") Long myFeedId, @Param("currentUserId") Long currentUserId);


//    // 개선 방식 (실패)
//    // 개선 방식 (JOIN + GROUP BY)
//    @Query("""
//    SELECT
//        f.id AS feedId,
//        u.id AS userId,
//        f.content AS content,
//        f.author AS author,
//        f.feedType AS feedType,
//        COUNT(DISTINCT fl.id) AS likeCount,
//        COUNT(DISTINCT c.id) FILTER (WHERE c.deletedAt IS NULL) AS commentCount,
//        CASE WHEN COUNT(DISTINCT flCurrent.id) > 0 THEN TRUE ELSE FALSE END AS isLiked,
//        f.createdAt AS createdAt,
//        f.updatedAt AS updatedAt,
//        f.deletedAt AS deletedAt
//    FROM Feed f
//    JOIN f.user u
//    LEFT JOIN FeedLike fl ON fl.feed = f
//    LEFT JOIN FeedLike flCurrent ON flCurrent.feed = f AND flCurrent.user.id = :currentUserId
//    LEFT JOIN Comment c ON c.feed = f
//    WHERE (:lastFeedId = 0 OR f.id < :lastFeedId) AND f.deletedAt IS NULL
//    GROUP BY f.id, u.id, f.content, f.author, f.feedType, f.createdAt, f.updatedAt, f.deletedAt
//    ORDER BY f.createdAt DESC
//    """)
//    List<FeedResponse> findFeedsResponseOptimized(@Param("lastFeedId") Long lastFeedId,
//                                                  @Param("size") int size,
//                                                  @Param("currentUserId") Long currentUserId);




}
