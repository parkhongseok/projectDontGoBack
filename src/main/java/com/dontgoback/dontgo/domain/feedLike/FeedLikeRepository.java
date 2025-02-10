package com.dontgoback.dontgo.domain.feedLike.repository;

import com.dontgoback.dontgo.domain.feedLike.entity.FeedLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedLikeRepository extends JpaRepository<FeedLike, Long> {
}
