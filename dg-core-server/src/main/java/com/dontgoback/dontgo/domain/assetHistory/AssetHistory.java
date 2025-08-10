package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "asset_history",
//        uniqueConstraints = @UniqueConstraint(
//                name = "uk_asset_history_user_snapshot_day",
//                columnNames = {"user_id", "snapshot_day"}
//        ),
        indexes = {
                @Index(name = "ix_asset_history_user_snapshot_day", columnList = "user_id, snapshot_day"),
                @Index(name = "ix_asset_history_user_changed_at_desc", columnList = "user_id, changed_at DESC")
        }
)
public class AssetHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    /** 스냅샷 금액(원 단위). 0 허용, 음수 금지(서비스 레이어에서 가드) */
    @Column(nullable = false)
    private long amount;

    /**
     * (옵션) 당일 적용된 변동 배수. 시뮬레이션이면 기록해두면 추적에 유용.
     * 실데이터 전환 시 null 또는 1.0로 둘 수 있음.
     */
    @Column
    private Double multiplier;

    @Column(nullable = false)
    private LocalDate snapshotDay;

    @CreatedDate
    private LocalDateTime changedAt;

    public String getAssetName(){
        return AssetFormatter.getNameFormat(amount);
    }

    public RedBlueType getAssetType() {
        return AssetFormatter.getTypeFormat(this.multiplier);
    }

    public static AssetHistory of(User user, long amount, java.time.LocalDate day, Double multiplier) {
        return AssetHistory.builder()
                .user(user)
                .amount(amount)
                .snapshotDay(day)
                .multiplier(multiplier != null ? multiplier : 1.0)
                .build();
    }
}
