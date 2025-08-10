package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.global.exception.UnauthorizedException;
import com.dontgoback.dontgo.global.resData.ResData;
import com.dontgoback.dontgo.interserver.extension.ExtensionServerProperties;
import com.dontgoback.dontgo.interserver.extension.asset.dto.FakeUpdateAssetResponse;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetRequest;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class InterServerAssetRequestServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private InterServerAssetRequestService assetRequestService;


    private final long userId = 42L;
    private final String jwt = "test.jwt.token";
    private final UpdateAssetRequest request = new UpdateAssetRequest(10000L);
    private final String targetUrl = "http://localhost:8082/msa/ext/api/update-asset/42";
    private final FakeExtensionServerProperties extProps =
            new FakeExtensionServerProperties("http://localhost:8082/msa/ext/api");

    public static class FakeExtensionServerProperties extends ExtensionServerProperties {
        public FakeExtensionServerProperties(String host) {
            this.setHost(host);
        }
    }

    @BeforeEach
    void init() {
        assetRequestService = new InterServerAssetRequestService(restTemplate, extProps);
    }

    /* ---------- 정상 응답 ---------- */
    @Test
    @DisplayName("정상 응답을 받을 경우 → 자산 정보를 반환한다")
    void should_return_asset_when_successful() {
        // given
        long asset = 1500L;
        UpdateAssetResponse responseBody = new FakeUpdateAssetResponse(userId, asset);
        ResData<UpdateAssetResponse> responseWrapper = ResData.of("S", "Success", responseBody);
        ResponseEntity<ResData<UpdateAssetResponse>> mockResponse =
                ResponseEntity.ok(responseWrapper);

        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(targetUrl),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        // when
        UpdateAssetResponse result = assetRequestService.updateAsset(userId, jwt, request);

        // then
        assertThat(result.getUpdatedAsset()).isEqualTo(asset);
    }

    /* ---------- 401 인증 실패 ---------- */
    @Test
    @DisplayName("확장 서버에서 401 인증 실패가 발생하면 예외를 던진다")
    void should_throw_exception_when_unauthorized() {
        // given
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.any(ParameterizedTypeReference.class)
        )).thenThrow(new UnauthorizedException("JWT 인증 실패"));

        // expect
        assertThatThrownBy(() -> assetRequestService.updateAsset(userId, jwt, request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("JWT 인증 실패");
    }

    /* ---------- 기타 예외 발생 ---------- */
    @Test
    @DisplayName("예상하지 못한 예외가 발생하면 RuntimeException으로 감싸서 던진다")
    void should_wrap_unexpected_exception() {
        // given
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.anyString(),
                ArgumentMatchers.eq(HttpMethod.POST),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("연결 실패"));

        // expect
        assertThatThrownBy(() -> assetRequestService.updateAsset(userId, jwt, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("자산 갱신 실패");
    }
}
