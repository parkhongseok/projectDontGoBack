# MSA 서버 간 통합 흐름 검증을 위한 테스트 전략

Date: 2025-08-07  
Status: Accepted

<br/>

## 맥락

Core 서버는 **인증 서버(토큰 발급)**, **확장 서버(자산 갱신)** 와 통신합니다.  
JWT 기반 인증·데이터 동기화·상태 반영이 동시에 일어나는 구조이므로,  
**“작은 단위 → 전 구간 통합”** 계층형 테스트로 신뢰성을 확보했습니다.

#### 테스트 요구 사항

| 계층       | 검증 번호 | 요약                                          |
| ---------- | --------- | --------------------------------------------- |
| Unit Test  | ①         | JWT 발급 성공/실패                            |
|            | ②         | 토큰 캐싱·만료·재발급                         |
|            | ③         | 토큰 자동 주입 및 재시도                      |
|            | ④         | 확장 서버 자산 요청 파싱                      |
| Flow Test  | ⑤         | 응답 기반 도메인(`User`, `AssetHistory`) 갱신 |
| End-to-End | ⑥         | 세 서버 연결된 전체 흐름                      |

<br/>
<br/>

## 결정

### 1 단계 | 단위(Unit) 테스트

| 대상 클래스                      | 핵심 검증                      | 비고                                         |
| -------------------------------- | ------------------------------ | -------------------------------------------- |
| `InterServerJwtRequestService`   | 인증 서버 ↔ JWT 발급 성공/실패 | `@SpringBootTest`                            |
| `InterServerTokenManager`        | 캐싱·만료·재발급               | `Clock.fixed` 사용                           |
| `InterServerApiExecutor`         | JWT 자동 주입, 401 재시도      | `Mockito` `UnauthorizedException` 시뮬레이션 |
| `InterServerAssetRequestService` | 응답 파싱·예외 래핑            | `RestTemplate` Mock                          |
| `InterServerAssetUpdateService`  | 트랜잭션 분리·도메인 갱신 호출 | `Repository` Mock                            |

<br/>
<br/>

### 2 단계 | 서비스(Flow) 테스트

- 대상 클래스: `InterServerAssetUpdateServiceTest`
- 예시:

  ```java
  // 외부 응답을 stub
  when(apiExecutor.executeWithToken(any()))
      .thenReturn(new UpdateAssetResponse(newAsset));

  updateService.updateAllActiveUsersAsset();   // 실행

  // AssetHistory + User.currentAssetHistory 검증
  assertThat(assetHistoryRepository.count()).isEqualTo(2);
  ```

  > _외부 호출은 Mock, DB 저장과 연관관계 갱신은 실제 수행_

<br/>
<br/>

### 3 단계 | 통합(End-to-End) 테스트

- 실제 인증·확장 서버를 기동하여 Core → Auth → Extention → DB 전 과정을 검증
- **대상 클래스**: `AssetUpdateIntegrationTest`

  | 단계 | 서버                               | 수행 기능                             |
  | ---- | ---------------------------------- | ------------------------------------- |
  | ①    | `core-server` → `auth-server`      | JWT 발급 요청                         |
  | ②    | `core-server` → `extention-server` | 자산 갱신 요청 (Authorization header) |
  | ③    | `core-server` DB                   | `AssetHistory` 저장 + `User` 갱신     |

  > 테스트 DB (H2) 와 실서버(`auth-server`, `extension-server`) 를 함께 띄워 검증하였습니다.

<br/>

- **전체 흐름**

  ```less
  [Core 서버 테스트 대상 흐름]
  ┌───────────────┐
  │  UserService  │
  └────┬──────────┘
        │
        ▼
  ┌─────────────────────────────────────┐
  │    InterServerAssetUpdateService    │
  └─────┬───────────────────────────┬───┘
        ▼                           ▼
  InterServerApiExecutor     persistAssetChange(@Transactional)
        │                           │
        ▼                           ▼
  InterServerTokenManager     UserRepository / AssetHistoryRepository
        │
        ▼
  InterServerJwtRequestService
        │
        ▼
  [Auth Server] - JWT 발급
        │
        ▼
  [Extentin Server] - 갱신된 자산 응답
  ```

<br/>
<br/>

## 결과

| 효과                   | 설명                                                  |
| ---------------------- | ----------------------------------------------------- |
| **계층별 리스크 격리** | 작은 메서드 오류부터 외부 장애까지 단계별로 탐지·해결 |
| **보안 흐름 검증**     | 실제 JWT 발급↔검증 과정을 통합 테스트로 확인          |
| **트랜잭션 안전성**    | 외부 실패 시 DB 미반영, 내부 실패 시 외부 영향 없음   |
| **테스트 커버리지**    | 단위 → 흐름 → 통합으로 모든 코드 경로를 실행          |

> 각 단계별 테스트 클래스는 [`/src/test/interserver`](/dg-core-server/src/test/java/com/dontgoback/dontgo/interserver/) 에서 확인하실 수 있습니다.
