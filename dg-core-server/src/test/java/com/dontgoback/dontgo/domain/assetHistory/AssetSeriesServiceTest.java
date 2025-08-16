package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistoryPoint;
import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistorySeriesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * AssetSeriesService 단위 테스트
 *
 * - Repository는 Mock으로 대체하여 서비스 로직만 검증합니다.
 * - 엔티티(AssetHistory)는 실제 클래스를 만들 필요 없이 Mockito mock으로
 *   getter만 스텁하여 테스트를 간결하게 유지합니다.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class AssetSeriesServiceTest {

    @Mock
    AssetHistoryRepository repo;

    @InjectMocks
    AssetSeriesService service;

    /* -------------- daily -------------- */

    @Test
    @DisplayName("daily: 원본 순서 유지 + 변화량/변화율 계산(소수 둘째 자리 반올림)")
    void daily_changes_and_order() {
        // given
        Long userId = 1L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-07-05");

        // 7/1: 1,000,000 → 변화 없음
        // 7/2: 1,020,000 → +20,000 / +2.00%
        // 7/5:   990,000 → -30,000 / -2.94%
        List<AssetHistory> rows = List.of(
                mockRow(LocalDate.parse("2025-07-01"), 1_000_000),
                mockRow(LocalDate.parse("2025-07-02"), 1_020_000),
                mockRow(LocalDate.parse("2025-07-05"),   990_000)
        );

        when(repo.findRange(userId, from, to)).thenReturn(rows);
        when(repo.findLatestChangedAt(userId)).thenReturn(LocalDateTime.parse("2025-07-05T12:00:00"));

        // when
        AssetHistorySeriesResponse res = service.getSeries(userId, from, to, "daily", null);

        // then
        assertThat(res.getInterval()).isEqualTo("daily");
        assertThat(res.getPoints()).hasSize(3);

        AssetHistoryPoint p1 = res.getPoints().get(0);
        AssetHistoryPoint p2 = res.getPoints().get(1);
        AssetHistoryPoint p3 = res.getPoints().get(2);

        assertThat(p1.getDate()).isEqualTo(LocalDate.parse("2025-07-01"));
        assertThat(p1.getAmount()).isEqualTo(1_000_000);
        assertThat(p1.getChangeAmount()).isNull();
        assertThat(p1.getChangePercent()).isNull();

        assertThat(p2.getDate()).isEqualTo(LocalDate.parse("2025-07-02"));
        assertThat(p2.getChangeAmount()).isEqualTo(20_000);
        assertThat(p2.getChangePercent()).isEqualTo(2.00);

        assertThat(p3.getDate()).isEqualTo(LocalDate.parse("2025-07-05"));
        assertThat(p3.getChangeAmount()).isEqualTo(-30_000);
        assertThat(p3.getChangePercent()).isEqualTo(-2.94);

        assertThat(res.getLatestChangedAt()).isEqualTo(LocalDateTime.parse("2025-07-05T12:00:00"));
        assertThat(res.getCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("daily: 이전 금액이 0원이면 변화율은 null 처리")
    void daily_prevZero_percentIsNull() {
        // given
        Long userId = 1L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-07-02");

        List<AssetHistory> rows = List.of(
                mockRow(LocalDate.parse("2025-07-01"), 0),
                mockRow(LocalDate.parse("2025-07-02"), 10_000)
        );
        when(repo.findRange(userId, from, to)).thenReturn(rows);
        when(repo.findLatestChangedAt(userId)).thenReturn(null);

        // when
        AssetHistorySeriesResponse res = service.getSeries(userId, from, to, "daily", null);

        // then
        AssetHistoryPoint p2 = res.getPoints().get(1);
        assertThat(p2.getChangeAmount()).isEqualTo(10_000);
        assertThat(p2.getChangePercent()).isNull(); // 0으로 나누기 회피 정책
    }

    /* -------------- weekly -------------- */

    @Test
    @DisplayName("weekly: 월요일 시작 주 기준, 각 주의 마지막 스냅샷 선택")
    void weekly_rollup_lastOfWeek() {
        // given
        Long userId = 1L;
        LocalDate from = LocalDate.parse("2025-06-30"); // 월요일
        LocalDate to   = LocalDate.parse("2025-07-12");

        // 주간 구성:
        // Week-1 (6/30~7/6): 6/30(100), 7/1(110), 7/4(120), 7/6(130) → 마지막: 7/6(130)
        // Week-2 (7/7~7/13): 7/7(140), 7/9(150), 7/12(160)        → 마지막: 7/12(160)
        List<AssetHistory> rows = List.of(
                mockRow(LocalDate.parse("2025-06-30"), 100),
                mockRow(LocalDate.parse("2025-07-01"), 110),
                mockRow(LocalDate.parse("2025-07-04"), 120),
                mockRow(LocalDate.parse("2025-07-06"), 130),
                mockRow(LocalDate.parse("2025-07-07"), 140),
                mockRow(LocalDate.parse("2025-07-09"), 150),
                mockRow(LocalDate.parse("2025-07-12"), 160)
        );
        when(repo.findRange(userId, from, to)).thenReturn(rows);
        when(repo.findLatestChangedAt(userId)).thenReturn(LocalDateTime.parse("2025-07-12T23:59:59"));

        // when
        AssetHistorySeriesResponse res = service.getSeries(userId, from, to, "weekly", null);

        // then
        assertThat(res.getInterval()).isEqualTo("weekly");
        assertThat(res.getPoints()).hasSize(2);

        AssetHistoryPoint w1 = res.getPoints().get(0);
        AssetHistoryPoint w2 = res.getPoints().get(1);

        assertThat(w1.getDate()).isEqualTo(LocalDate.parse("2025-07-06"));
        assertThat(w1.getAmount()).isEqualTo(130);

        assertThat(w2.getDate()).isEqualTo(LocalDate.parse("2025-07-12"));
        assertThat(w2.getAmount()).isEqualTo(160);

        // 주간 시리즈에서도 변화량/율은 "전 포인트" 기준으로 계산됨
        assertThat(w2.getChangeAmount()).isEqualTo(160 - 130);
        assertThat(w2.getChangePercent()).isEqualTo( // (30/130)*100 = 23.08 → 23.08
                23.08
        );
    }

    /* -------------- monthly -------------- */

    @Test
    @DisplayName("monthly: 각 월의 마지막 스냅샷 선택")
    void monthly_rollup_lastOfMonth() {
        // given
        Long userId = 1L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-08-31");

        // 7월: 7/01(100), 7/15(120), 7/31(140) → 마지막: 7/31(140)
        // 8월: 8/01(150), 8/20(130)           → 마지막: 8/20(130) (31일 데이터 없어도 OK)
        List<AssetHistory> rows = List.of(
                mockRow(LocalDate.parse("2025-07-01"), 100),
                mockRow(LocalDate.parse("2025-07-15"), 120),
                mockRow(LocalDate.parse("2025-07-31"), 140),
                mockRow(LocalDate.parse("2025-08-01"), 150),
                mockRow(LocalDate.parse("2025-08-20"), 130)
        );
        when(repo.findRange(userId, from, to)).thenReturn(rows);
        when(repo.findLatestChangedAt(userId)).thenReturn(LocalDateTime.parse("2025-08-20T10:00:00"));

        // when
        AssetHistorySeriesResponse res = service.getSeries(userId, from, to, "monthly", null);

        // then
        assertThat(res.getInterval()).isEqualTo("monthly");
        assertThat(res.getPoints()).hasSize(2);

        AssetHistoryPoint m7 = res.getPoints().get(0);
        AssetHistoryPoint m8 = res.getPoints().get(1);

        assertThat(m7.getDate()).isEqualTo(YearMonth.of(2025, 7).atEndOfMonth());
        assertThat(m7.getAmount()).isEqualTo(140);

        assertThat(m8.getDate()).isEqualTo(LocalDate.parse("2025-08-20"));
        assertThat(m8.getAmount()).isEqualTo(130);

        // 월간 변화량/율
        assertThat(m8.getChangeAmount()).isEqualTo(130 - 140);
        assertThat(m8.getChangePercent()).isEqualTo(-7.14); // ( -10 / 140 ) * 100 = -7.14
    }

    /* -------------- decimation -------------- */

    @Test
    @DisplayName("limit: 포인트가 limit를 초과하면 균등 샘플링 - 첫/마지막 포함")
    void decimation_evenSampling_keepsFirstAndLast() {
        // given
        Long userId = 1L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-07-10");
        // 10일치 데이터 생성
        List<AssetHistory> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            rows.add(mockRow(from.plusDays(i), 1_000_000 + i * 1_000));
        }
        when(repo.findRange(userId, from, to)).thenReturn(rows);
        when(repo.findLatestChangedAt(userId)).thenReturn(LocalDateTime.parse("2025-07-10T00:00:00"));

        // when: limit=3 → 3포인트로 줄어야 함 (첫/마지막 포함)
        AssetHistorySeriesResponse res = service.getSeries(userId, from, to, "daily", 3);

        // then
        assertThat(res.getPoints()).hasSize(3);
        assertThat(res.getPoints().get(0).getDate()).isEqualTo(LocalDate.parse("2025-07-01"));
        assertThat(res.getPoints().get(2).getDate()).isEqualTo(LocalDate.parse("2025-07-10"));
    }

    /* -------------- latestChangedAt / ETag -------------- */

    @Test
    @DisplayName("latestChangedAt: 리포지토리 값을 그대로 반환")
    void latestChangedAt_fromRepo() {
        Long userId = 10L;
        LocalDateTime ts = LocalDateTime.parse("2025-08-10T09:30:00");
        when(repo.findLatestChangedAt(userId)).thenReturn(ts);

        assertThat(service.getLatestChangedAt(userId)).isEqualTo(ts);
    }

    @Test
    @DisplayName("buildEtag: 같은 입력이면 항상 같은 ETag를 돌려준다")
    void buildEtag_isDeterministic() {
        Long userId = 1L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-07-31");
        String interval = "daily";
        LocalDateTime latest = LocalDateTime.parse("2025-07-31T10:00:00");

        String e1 = service.buildEtag(userId, from, to, interval, latest);
        String e2 = service.buildEtag(userId, from, to, interval, latest);

        assertThat(e1).isEqualTo(e2);
        assertThat(e1).startsWith("\"asset-");
    }

    /* =================== 헬퍼 =================== */

    /**
     * AssetHistory 엔티티를 Mockito mock으로 만들어
     * 스냅샷 날짜/금액만 세팅합니다.
     * getAssetType()은 서비스 로직에서 값만 전달하므로 굳이 검증하지 않습니다.
     */
    private AssetHistory mockRow(LocalDate day, long amount) {
        AssetHistory m = mock(AssetHistory.class, RETURNS_DEEP_STUBS);
        when(m.getSnapshotDay()).thenReturn(day);   // 주/월 그룹핑/정렬에 모두 사용됨
        when(m.getAmount()).thenReturn(amount);     // 변화량/변화율 계산에 사용됨
        // getAssetType()은 기본값(null) 그대로 두기 — 스터빙하지 않음
        return m;
    }
}
