package com.dontgoback.dontgo.domain.comment.dto;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.UserRole;

import java.time.LocalDateTime;

public interface CommentResponse {
    Long getCommentId(); // 컬럼 별칭 "commentId"와 매핑
    Long getFeedId(); // 컬럼 별칭 "feedId"와 매핑
    Long getUserId(); // 컬럼 별칭 "userId"와 매핑
    String getContent();
    String getAuthor();
    UserRole getUserRole();
    String getCommentType();
    int getLikeCount();
    int getSubCommentCount();
    boolean getIsLiked();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    LocalDateTime getDeletedAt();

    //필요한 필드만 선택해서 가져옴 → 성능 최적화
    //연관된 엔티티를 불필요하게 로드하지 않음 → Lazy Loading 활용 가능
    //클라이언트에서 처리하기 쉽게 변환 가능
}
