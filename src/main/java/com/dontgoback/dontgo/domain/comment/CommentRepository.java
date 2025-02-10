package com.dontgoback.dontgo.domain.comment.repository;

import com.dontgoback.dontgo.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
