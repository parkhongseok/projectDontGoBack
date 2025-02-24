package com.dontgoback.dontgo.domain.comment;

import com.dontgoback.dontgo.domain.comment.dto.CommentsResponse;
import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentsResponse getCommentsResponse(Long feedId){
        return new CommentsResponse(commentRepository.findCommentsResponse(feedId));
    }

    public Comment createDummyComment(Feed feed, User user, String content){
        Comment comment = Comment.builder()
                                .feed(feed)
                                .user(user)
                                .author(user.getUserAsset())
                                .commentType(user.getUserType())
                                .content(content)
                                .build();
        commentRepository.save(comment);
    return comment;
    }

}
