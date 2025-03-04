package com.dontgoback.dontgo.domain.comment;

//import com.dontgoback.dontgo.domain.commentLike.CommentLike;
import com.dontgoback.dontgo.domain.feed.Feed;
//import com.dontgoback.dontgo.domain.notification.Notification;
//import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static com.dontgoback.dontgo.global.util.GlobalValues.MAX_TEXT_LENGTH;

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

    // 당연 지사 유저 이름이지만, 유저 이름은 매일 바뀌니까 다로 저장할 필요가 있음
    private String author;
    // 당연 지사 피드 타입과 동일하겠지만, 추후 다른 타입에도 댓글을 남길 수 있도록 변경을 고려
    private RedBlueType commentType;

    @Column(nullable = false, length = MAX_TEXT_LENGTH)
    private String content;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
    // 소프트 삭제 구현 시 쿼리 수정 필요
}