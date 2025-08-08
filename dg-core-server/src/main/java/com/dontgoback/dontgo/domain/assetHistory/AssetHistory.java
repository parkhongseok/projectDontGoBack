package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.jpa.BaseEntity;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Builder
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private long amount;

    @CreatedDate
    private LocalDateTime changedAt;

    public String getAssetName(){
        return AssetFormatter.getNameFormat(amount);
    }

    public RedBlueType getAssetType() {
        return AssetFormatter.getTypeFormat(amount);
    }

    public static AssetHistory of(User user, long amount) {
        return AssetHistory.builder()
                .user(user)
                .amount(amount)
                .changedAt(LocalDateTime.now()) // 현재 시점 기록
                .build();
    }
}
