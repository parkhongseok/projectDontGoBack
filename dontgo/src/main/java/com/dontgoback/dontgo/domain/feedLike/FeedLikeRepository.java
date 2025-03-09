package com.dontgoback.dontgo.domain.feedLike;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, FeedLikeId> {
    int countByFeedId(Long feedId);
}
