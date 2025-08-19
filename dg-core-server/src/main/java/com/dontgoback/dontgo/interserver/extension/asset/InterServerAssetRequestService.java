package com.dontgoback.dontgo.interserver.extension.asset;

import com.dontgoback.dontgo.global.exception.UnauthorizedException;
import com.dontgoback.dontgo.global.resData.ResData;
import com.dontgoback.dontgo.global.resData.ResDataParser;
import com.dontgoback.dontgo.interserver.extension.ExtensionServerProperties;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetRequest;
import com.dontgoback.dontgo.interserver.extension.asset.dto.UpdateAssetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Slf4j
@Component
public class InterServerAssetRequestService {

    private final RestTemplate restTemplate;
    private final ExtensionServerProperties extServerProps;

    public InterServerAssetRequestService(
            @Qualifier("extensionRestTemplate") RestTemplate restTemplate,
            ExtensionServerProperties extServerProps
    ) {
        this.restTemplate = restTemplate;
        this.extServerProps = extServerProps;
    }


     /**
     * 확장 서버에 자산 갱신 요청을 보냅니다.
     *
     * @param userId 사용자 식별자
     * @param jwt JWT 인증 토큰
     * @param request 갱신 요청 데이터 (자산)
     * @return UpdateAssetResponse (새로운 자산 값)
     */
    public UpdateAssetResponse updateAsset(long userId, String jwt, UpdateAssetRequest request, LocalDate snapshotDay) {
        String url = String.format("%s/msa/ext/api/update-asset/%d", extServerProps.getHost(), userId);
        log.debug("자산 갱신 요청 → URL: {}, asset: {}", url, request.getAsset());

        HttpEntity<UpdateAssetRequest> requestEntity = buildRequestEntity(request, jwt);

        try {
            ResponseEntity<ResData<UpdateAssetResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResData<UpdateAssetResponse>>() {}
            );
            // ResDataParser 유틸리티 사용하여 응답 파싱 + 실패 처리
            UpdateAssetResponse data = ResDataParser.extract(response);

            log.info("자산 갱신 성공 - userId: {}, 결과 자산: {}", userId, data.getUpdatedAsset());
            return data;

        } catch (UnauthorizedException e) {
            throw e;

        } catch (Exception e) {
            log.error("자산 갱신 실패 - userId: {}, 이유: {}", userId, e.getMessage(), e);
            throw new RuntimeException("자산 갱신 실패", e);
        }
    }

    /**
     * JWT와 함께 JSON 본문을 포함한 HttpEntity를 생성합니다.
     */
    private HttpEntity<UpdateAssetRequest> buildRequestEntity(UpdateAssetRequest request, String jwt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwt);

        return new HttpEntity<>(request, headers);
    }
}
