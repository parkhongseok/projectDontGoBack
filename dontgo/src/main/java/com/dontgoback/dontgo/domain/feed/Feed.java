package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.comment.Comment;
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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "feeds")
public class Feed extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    // 유저 이름(userAsset)이 매일 갱신되니까, 게시글은 이를 갖고 있어야함
    @Column(nullable = false)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RedBlueType feedType;

    @Column(nullable = false, length = MAX_TEXT_LENGTH)
    private String content;

    private LocalDateTime deletedAt;

}
