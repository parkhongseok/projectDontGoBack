package com.dontgoback.dontgo.domain.feed;
import com.dontgoback.dontgo.domain.feed.dto.FeedResponse;
import com.dontgoback.dontgo.domain.feed.dto.FeedsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    @Query("SELECT " +
            "f.id AS feedId, " +
            "u.id AS userId, " +
            "f.content AS content, " +
            "u.userAsset AS userName, " +
            "f.feedType AS feedType, " +
            "(SELECT COUNT(l) FROM FeedLike l WHERE l.feed = f) AS likeCount, " +
            "(SELECT COUNT(c) FROM Comment c WHERE c.feed = f) AS commentCount, " +
            "f.createdAt AS createdAt, " +
            "f.updatedAt AS updatedAt, " +
            "f.deletedAt AS deletedAt " +
            "FROM Feed f JOIN f.user u " +
            "ORDER BY createdAt DESC" )
    List<FeedResponse> findFeedsResponse();

    @Query("SELECT " +
            "f.id AS feedId, " +
            "u.id AS userId, " +
            "f.content AS content, " +
            "u.userAsset AS userName, " +
            "f.feedType AS feedType, " +
            "(SELECT COUNT(l) FROM FeedLike l WHERE l.feed = f) AS likeCount, " +
            "(SELECT COUNT(c) FROM Comment c WHERE c.feed = f) AS commentCount, " +
            "f.createdAt AS createdAt, " +
            "f.updatedAt AS updatedAt, " +
            "f.deletedAt AS deletedAt " +
            "FROM Feed f JOIN f.user u " +
            "WHERE f.id = :myFeedId")
    Optional<FeedResponse> findFeedResponseById(@Param("myFeedId") Long myFeedId);

}
