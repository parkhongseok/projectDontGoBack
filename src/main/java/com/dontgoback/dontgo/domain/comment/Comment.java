package com.dontgoback.dontgo.domain.comment;

//import com.dontgoback.dontgo.domain.commentLike.CommentLike;
import com.dontgoback.dontgo.domain.feed.Feed;
//import com.dontgoback.dontgo.domain.notification.Notification;
//import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
//    private User user;
//    private List<Notification> notifications;
//    private List<CommentLike> commentLikes;
//    private Feed feed;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FEED_ID")
    private Feed feed;

    private String content;
    private LocalDateTime deletedAt;
}