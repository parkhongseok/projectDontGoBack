package com.dontgoback.dontgo.domain.comment;

//import com.dontgoback.dontgo.domain.commentLike.CommentLike;
import com.dontgoback.dontgo.domain.feed.Feed;
//import com.dontgoback.dontgo.domain.notification.Notification;
//import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FEED_ID")
    private Feed feed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAREUNT_COMMENT_ID")
    private Comment parentComment;

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
    // 소프트 삭제 구현 시 쿼리 수정 필요
}