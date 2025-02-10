package com.dontgoback.dontgo.domain.feed.entity;

import com.dontgoback.dontgo.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Feed extends BaseEntity {
    private long userId;
    private String feedType;    // up or dawn
    private String content;
    private LocalDateTime deletedAt;    // 'active' or 'deleted'
}
