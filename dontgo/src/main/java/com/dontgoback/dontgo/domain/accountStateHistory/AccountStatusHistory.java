package com.dontgoback.dontgo.domain.accountStateHistory;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "account_status_histories")
public class AccountStatusHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에게 키 생성 역을 넘김
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private LocalDateTime changedAt;    // 상태 시작일

    private LocalDateTime endedAt;

    // 예: 사유, 관리자 입력 메모 등
    private String reason;
}
