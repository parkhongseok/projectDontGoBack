package com.dontgoback.dontgo.domain.feedLike.entity;

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
public class FeedLike {
    private long feedId;
    private long userId;
    private LocalDateTime createAt;
}
