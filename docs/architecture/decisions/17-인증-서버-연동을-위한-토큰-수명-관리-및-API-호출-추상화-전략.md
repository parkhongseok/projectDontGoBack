# 인증 서버 연동을 위한 토큰 수명 관리 및 API 호출 추상화 전략

Date: 2025-08-07  
Status: Accepted

<br/>

## 맥락

DontGoBack 프로젝트는 마이크로서비스 아키텍처(MSA) 기반으로 전환 중에 있으며,  
코어 서버는 인증 서버로부터 JWT를 발급받아 확장 서버들과의 보안 통신을 수행해야 합니다.

초기에는 다음과 같은 방식으로 토큰을 처리하였습니다:

- 단순한 `RestTemplate` 호출로 JWT를 발급 요청
- API 요청마다 매번 토큰 발급 로직을 포함
- 토큰 만료 시점이나 예외에 대한 대응이 부족

이로 인해 **중복 코드**, **예외 처리 누락**, **테스트 어려움** 등의 문제가 발생하였고,  
보안 흐름을 일관되게 유지하기 위해 구조적 리팩터링이 필요했습니다.

<br/><br/>

## 결정

다음과 같은 구조로 토큰 발급과 사용을 리팩터링하였습니다:

### 1. 클래스 구조

- `interserver.auth/`

  | 구성 요소                      | 역할                            | 특이사항                                    |
  | ------------------------------ | ------------------------------- | ------------------------------------------- |
  | `InterServerJwtRequestService` | 인증 서버에 JWT 발급 요청       | DTO 기반 요청 및 응답 구조화                |
  | `InterServerTokenHolder`       | 발급된 JWT 보관 객체            | 토큰 문자열 + 발급 시각 보유                |
  | `InterServerTokenManager`      | 토큰 유효성 검사 및 자동 재발급 | 토큰 캐싱 및 만료 시점 자동 감지            |
  | **`InterServerApiExecutor`**   | **서버 간 통신 래퍼**           | **JWT 주입** 및 **재발급 재시도** 자동 처리 |

<br/>

- `interserver.extension/`

  | 구성 요소                        | 주요 역할                       | 특이사항                    |
  | -------------------------------- | ------------------------------- | --------------------------- |
  | `InterServerAssetUpdateService`  | **전체 갱신 흐름 제어**         | 활성화 유저 순회, 갱신 요청 |
  | `InterServerAssetRequestService` | 확장 서버에 자산 갱신 요청 전송 |                             |

<br/>
<br/>

### 2. 의존성 흐름

```java
InterServerAssetUpdateService
├─── InterServerApiExecutor
│     └── InterServerTokenManager
│         ├── InterServerJwtRequestService
│         │   └── RestTemplate (to AuthServer)
│         └── InterServerTokenHolder (in-memory)
└─── InterServerAssetRequestService
      └── RestTemplate (to ExtensionServer)
```

주요 흐름은 아래와 같습니다.

- 자산 갱신 요청 시 → 래퍼 호출 → 토큰 발급 → 외부 요청 전송 → 응답 → DB 저장
  > 갱신 요청 시점은 배치로 구현 예정

<br/>
<br/>

### 3. API 호출 흐름

다음과 같이 단순화하여,  
`확장 서버`로의 요청마다 `인증 서버`로의 토큰 발급 및 예외 처리를 신경쓰지 않아도 되도록 수정하였습니다.

```java
UpdateAssetResponse result = apiExecutor.executeWithToken(jwt ->
    assetRequestService.updateAsset(userId, jwt, request) // 확장 서버로의 요청 시 래퍼가 일관된 토큰 처리
);
```

위 구조는 다음과 같은 원칙에 따라 설계되었습니다:

- **JWT는 메모리에 5분간 캐싱**, 만료 시 자동 재요청

- **401 Unauthorized 응답 발생 시**, 내부적으로 `forceRefresh()` → 재시도

- 호출자는 오직 로직에 집중, 인증 흐름은 모두 내부적으로 처리

<br/>
<br/>

## 결과

본 구조는 다음과 같은 효과를 가져왔습니다:

- **보안 흐름이 일관되게 유지됨**  
  → 모든 서버 간 통신에 동일한 방식으로 JWT가 주입되고, 만료 및 예외 대응이 통일됨

- **API 호출자는 인증 흐름에 신경쓰지 않아도 됨**  
  → `Function<String, T>` 형태의 람다로 핵심 로직만 전달

- **유지보수성과 테스트 용이성 증가**  
  → Mock 기반 단위 테스트에서 각 계층을 독립적으로 검증 가능

- **향후 확장성 확보**  
  → 서버 수 증가, 인증 방식 변경 시에도 기존 인터페이스 변경 없이 내부 구현만 수정 가능

<br/>

이 구조는 특히 아래와 같은 흐름에 적합합니다:

- 외부 인증 서버와 연동이 필요한 MSA 환경
- 토큰 수명이 짧고, 자동 갱신이 필요한 구조
- 보안 흐름의 일관성과 추상화가 중요한 서비스 구조

> 관련 코드들은 [`/src/main/.../interserver/auth/jwt`](/dg-core-server/src/main/java/com/dontgoback/dontgo/interserver/auth/jwt/) 에서 확인하실 수 있습니다.
