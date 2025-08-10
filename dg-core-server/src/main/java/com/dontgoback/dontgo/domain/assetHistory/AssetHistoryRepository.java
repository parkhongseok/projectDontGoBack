package com.dontgoback.dontgo.domain.assetHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    List<AssetHistory> findAllByUserId(Long userId);
}
