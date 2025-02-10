package com.dontgoback.dontgo.domain.comment.entity;

import com.dontgoback.dontgo.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
    private long userId;
    private long feedId;
    private long parentId;
    private String commnetType;
    private String content;
    private LocalDateTime deletedAt;
}