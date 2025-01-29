package com.dontgoback.dontgo.domain.feed.repository;
import com.dontgoback.dontgo.domain.feed.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
