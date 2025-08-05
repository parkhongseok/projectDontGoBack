package com.dontgoback.dontgo.global.jpa.EmbeddedTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public class FeedLikeId implements Serializable {
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "feed_id", nullable = false)
    private Long feedId;

    // equals와 hashCode 오버라이드
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeedLikeId that = (FeedLikeId) o;

        if (!Objects.equals(userId, that.userId)) return false;
        return Objects.equals(feedId, that.feedId);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (feedId != null ? feedId.hashCode() : 0);
        return result;
    }
}