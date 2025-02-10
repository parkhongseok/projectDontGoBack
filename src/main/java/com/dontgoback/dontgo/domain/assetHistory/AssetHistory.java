package com.dontgoback.dontgo.domain.asset;

import com.dontgoback.dontgo.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asset extends BaseEntity {
    private long asset;
}
