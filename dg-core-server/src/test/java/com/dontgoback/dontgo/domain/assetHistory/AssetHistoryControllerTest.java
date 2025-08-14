package com.dontgoback.dontgo.domain.assetHistory;

import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistoryPoint;
import com.dontgoback.dontgo.domain.assetHistory.dto.AssetHistorySeriesResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

// JPA 메타모델을 목빈으로 주입해 Auditing 초기화가 실패하지 않도록 함
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AssetHistoryController MockMvc 테스트
 * - 200 OK / 304 Not Modified / 400 Bad Request
 */
@WebMvcTest(controllers = AssetHistoryController.class)
@AutoConfigureMockMvc(addFilters = false) // 보안 필터가 있으면 비활성화(필요 시)
class AssetHistoryControllerTest {

    @Autowired
    MockMvc mockMvc;

    // 컨트롤러 의존 서비스는 목으로 대체
    @MockBean
    AssetSeriesService assetSeriesService;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private static final String BASE = "/api/v1/users/{userId}/asset-histories";

    @Test
    @DisplayName("GET /asset-histories - 200 OK, ResData 래핑 및 ETag 헤더 포함")
    void getSeries_ok_200() throws Exception {
        Long userId = 123L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-07-05");
        String interval = "daily";
        String etag = "\"asset-12345\"";

        AssetHistorySeriesResponse series = sampleSeries(userId, from, to, interval);

        Mockito.when(assetSeriesService.getSeries(eq(userId), eq(from), eq(to), eq(interval), isNull()))
                .thenReturn(series);
        Mockito.when(assetSeriesService.buildEtag(eq(userId), eq(from), eq(to), eq(interval), any()))
                .thenReturn(etag);

        mockMvc.perform(get(BASE, userId)
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("interval", interval))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ETAG, etag))
                .andExpect(jsonPath("$.resultCode").value("S-200"))
                .andExpect(jsonPath("$.message", containsString("자산 시계열")))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.interval").value(interval))
                .andExpect(jsonPath("$.data.points", hasSize(3)))
                .andExpect(jsonPath("$.data.points[0].date").value("2025-07-01"))
                .andExpect(jsonPath("$.data.points[0].amount").value(1_000_000))
                .andExpect(jsonPath("$.data.points[1].changeAmount").value(20_000))
                .andExpect(jsonPath("$.data.points[1].changePercent").value(2.0));
    }

    @Test
    @DisplayName("GET /asset-histories - 304 Not Modified (If-None-Match == ETag)")
    void getSeries_notModified_304() throws Exception {
        Long userId = 123L;
        LocalDate from = LocalDate.parse("2025-07-01");
        LocalDate to   = LocalDate.parse("2025-07-05");
        String interval = "daily";
        String etag = "\"asset-12345\"";

        AssetHistorySeriesResponse series = sampleSeries(userId, from, to, interval);

        Mockito.when(assetSeriesService.getSeries(eq(userId), eq(from), eq(to), eq(interval), isNull()))
                .thenReturn(series);
        Mockito.when(assetSeriesService.buildEtag(eq(userId), eq(from), eq(to), eq(interval), any()))
                .thenReturn(etag);

        mockMvc.perform(get(BASE, userId)
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("interval", interval)
                        .header(HttpHeaders.IF_NONE_MATCH, etag))
                .andExpect(status().isNotModified())
                .andExpect(header().string(HttpHeaders.ETAG, etag))
                .andExpect(content().string("")); // 304는 본문 없음
    }

    @Test
    @DisplayName("GET /asset-histories - 400 Bad Request (from > to)")
    void getSeries_badRequest_400() throws Exception {
        Long userId = 123L;
        LocalDate from = LocalDate.parse("2025-07-10");
        LocalDate to   = LocalDate.parse("2025-07-05");

        mockMvc.perform(get(BASE, userId)
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("F-400"))
                .andExpect(jsonPath("$.message", containsString("늦을 수 없습니다")));
    }

    /* ---------------------- helpers ---------------------- */

    private AssetHistorySeriesResponse sampleSeries(Long userId, LocalDate from, LocalDate to, String interval) {
        var p1 = AssetHistoryPoint.builder()
                .date(LocalDate.parse("2025-07-01"))
                .amount(1_000_000)
                .changeAmount(null)
                .changePercent(null)
                .type(null)
                .build();
        var p2 = AssetHistoryPoint.builder()
                .date(LocalDate.parse("2025-07-02"))
                .amount(1_020_000)
                .changeAmount(20_000L)
                .changePercent(2.0)
                .type(null)
                .build();
        var p3 = AssetHistoryPoint.builder()
                .date(LocalDate.parse("2025-07-05"))
                .amount(990_000)
                .changeAmount(-30_000L)
                .changePercent(-2.94)
                .type(null)
                .build();

        return AssetHistorySeriesResponse.builder()
                .userId(userId)
                .from(from)
                .to(to)
                .interval(interval)
                .points(List.of(p1, p2, p3))
                .latestChangedAt(LocalDateTime.parse("2025-07-05T12:00:00"))
                .count(3)
                .build();
    }
}