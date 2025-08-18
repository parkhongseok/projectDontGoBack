package com.dontgoback.dontgo.global.jpa.EmbeddedTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public class CommentLikeId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "comment_id")
        private Long commentId;

        // equals와 hashCode 오버라이드
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CommentLikeId that = (CommentLikeId) o;

            if (!Objects.equals(userId, that.userId)) return false;
            return Objects.equals(commentId, that.commentId);
        }
        @Override
        public int hashCode() {
            int result = userId != null ? userId.hashCode() : 0;
            result = 31 * result + (commentId != null ? commentId.hashCode() : 0);
            return result;
        }
    }