package com.dontgoback.dontgo.global.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@MappedSuperclass // 부모 클래스에서 테이블과 매핑하지 않고, 자식에게 상속 후 자식이 매핑됨
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@ToString
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에게 키 생성 역을 넘김
    private Long id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    protected LocalDateTime updatedAt;
    // Feed 엔티티에서 이 값을 강제로 바꾸는 경우 존재
}
