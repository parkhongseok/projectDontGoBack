package com.dontgoback.dontgo.domain.feedLike;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name="feed_likes")
public class FeedLike {

    @EmbeddedId
    private FeedLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @MapsId("feedId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feed_id", insertable = false, updatable = false)
    private Feed feed;
}
