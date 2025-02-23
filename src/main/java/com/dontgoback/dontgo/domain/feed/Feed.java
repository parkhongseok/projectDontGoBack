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

    @Enumerated(EnumType.STRING)
    private RedBlueType feedType;

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
}
