package com.dontgoback.dontgo.domain.comment.service;

import com.dontgoback.dontgo.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public long getCommentCount(long id){
        return commentRepository.count();
    }
}
