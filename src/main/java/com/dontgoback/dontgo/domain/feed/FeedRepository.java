package com.dontgoback.dontgo.domain.feed.repository;
import com.dontgoback.dontgo.domain.feed.entity.Feed;
import com.dontgoback.dontgo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
}
