package com.dontgoback.dontgo.domain.assetHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {
    List<AssetHistory> findAllByUserId(Long userId);

    @Query("""
        select a
        from AssetHistory a
        where a.user.id = :userId
          and a.snapshotDay between :from and :to
        order by a.snapshotDay asc
    """)
    List<AssetHistory> findRange(Long userId, LocalDate from, LocalDate to);

    // 최신 변경 시각(ETag/Last-Modified용)
    @Query("""
        select max(a.changedAt)
        from AssetHistory a
        where a.user.id = :userId
    """)
    LocalDateTime findLatestChangedAt(Long userId);

    boolean existsByUserIdAndSnapshotDay(Long userId, java.time.LocalDate snapshotDay);

    Optional<AssetHistory> findByUserIdAndSnapshotDay(Long uid, LocalDate d);
}
