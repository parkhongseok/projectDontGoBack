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


    // Feed update -> save 시, save의 반환 객체는, DB다녀오기 전이라서, UpdateAt정보 갱신 전이라서 수동으로 갱신
    // DB값과 차이 존재함 (~0.001초)
    // 무결성이 중요한 경우라면 사용 ㄴㄴ (영속성 컨텍스트 반영 전 문제애 대한 임시 방편)
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    } // 수동으로 updatedAt 갱신

}
