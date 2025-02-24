package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.comment.Comment;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.BaseEntity;

import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "feeds")
public class Feed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToMany(mappedBy = "feed")
    private List<Comment> comment;

    // 유저 이름(userAsset)이 매일 갱신되니까, 게시글은 이를 갖고 있어야함
    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RedBlueType feedType;

    @Column(nullable = false)
    private String content;

    private LocalDateTime deletedAt;

    // Feed update 후 반환 객체에서 사용 중, 완전히 DB값과 일치하지 않음.
    // (영속성 컨텍스트 반영 전 문제애 대한 임시 방편)
    // 무결성이 중요한 경우라면 사용 ㄴㄴ
    @PreUpdate
    public void preUpdate() {
        super.updatedAt = LocalDateTime.now(); // 수동으로 updatedAt 갱신
    }
}
