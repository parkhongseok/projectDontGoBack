package com.dontgoback.dontgo.domain.feedLike;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="feed_likes")
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class FeedLike {

    @EmbeddedId
    private FeedLikeId id;

    // 위에서 이미 아래 두 속성이 있으므로, 해당 값들을 추가로 저장 x
    // feed에서 해당 엔티티를 참조하고 있기에, 외례키 지정 필요
//    중복 컬럼 문제는 @EmbeddedId와 @ManyToOne을 함께 사용할 때 발생
    @ManyToOne
    @JoinColumn(name = "feed_id", insertable = false, updatable = false) // 중복 컬럼 문제
    private Feed feed;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @CreatedDate
    protected LocalDateTime createAt;

    public FeedLike(FeedLikeId id) {
        this.id = id;
    }
}
