# OAuth2 및 JWT기반 인증/인가 아키텍처

Date: 2025-02-25

## 상태

적용 중

## 요약

!["OAuth2 Architecture"](../src/08-OAuth2-JWT-인증-인가-흐름-요약.png)

> 사용자는 구글을 통해 인증하고, 백엔드는 사용자 정보를 바탕으로 JWT를 발급하여 인증 상태를 유지합니다.

| 항목        | 설명                                                           |
| ----------- | -------------------------------------------------------------- |
| 인증 수단   | Google OAuth2 (Authorization Code Flow)                        |
| 사용자 식별 | JWT Access Token 활용                                          |
| 재인증 방식 | Refresh Token을 통한 토큰 재발급                               |
| 저장 위치   | Access Token → localStorage / Refresh Token → HttpOnly Cookie  |
| 보안 강화   | HTTPS 전용 통신, HttpOnly 쿠키 설정, Spring Security 기반 구성 |

<br/>
<br/>

## 맥락

본 프로젝트에서의 인증 관련 요구사항은 다음과 같았습니다.

```
① 사용자는 간편하게 로그인할 수 있어야 한다.
② 인증 상태는 일정 시간 동안 안전하게 유지되어야 한다.
③ 사용자 정보는 보안적으로 보호되어야 한다.
```

이에 따라 인증과 인가 흐름은 아래 세 가지 기준을 중심으로 설계하였습니다.

<br/>

---

<br/>

### ① 간편한 로그인 수단 선택: `Google OAuth2`

- 사용자가 매번 ID/PW를 입력하는 방식은 번거롭기 때문에, **소셜 로그인 방식이 적합**하다고 판단했습니다
- 단, 다양한 소셜 로그인 제공 시 혼란 가능성을 고려하여, **Google 단일 플랫폼 기반 로그인**을 채택하였습니다.

<br/>

---

<br/>

### ② 인증 상태 유지 방식 결정: `JWT 기반 인증 `

- 세션-쿠키 방식은 서버 측 세션 관리를 요구하므로 확장성에 불리하다고 판단했습니다.
- 따라서 **OAuth2를 통한 사용자 인증 위임 후, JWT를 활용한 토큰 기반 인증 방식**을 채택하였습니다.
  - `Access Token`은 짧은 유효 기간을 갖고,
  - `Refresh Token`은 장기적으로 재발급을 위한 용도로 사용됩니다.
- 클라이언트 측에서는 이 두 토큰을 각각 `localStorage`, `cookie`에 저장합니다.

<br/>

---

<br/>

### ③ 보안 강화 및 서버 설정: `Spring Security 적용`

- **Spring Security** 위에서 OAuth2와 JWT 기반 인증을 구현하여 보안을 강화하였습니다.
- 토큰은 반드시 HTTPS 환경에서만 통신되도록 하고, 민감한 정보가 담긴 쿠키에는 다음 옵션을 적용하였습니다:

  - `Secure` : HTTPS 환경에서만 전송
  - `HttpOnly` : JavaScript 접근 차단

- 이를 위해 사전에 다음과 같은 작업이 선행되었습니다.

  <pre>
  1. EC2에 고정 IP 할당
  2. 도메인 연결 (A 레코드 설정)
  3. SSL 인증서 발급 및 Nginx HTTPS 리버스 프록시 구성
  </pre>

<br/>
<br/>

## 결정

!["OAuth2 Architecture"](../src/08-OAuth2-JWT-인증-인가-흐름.png)

JWT 기반 Google OAuth 인증 인가 아키텍처는 아래와 같이 총 세 주체로 구성됩니다.

- **사용자 브라우저 (Resource Owner)**

- **서비스 서버 (Client Application)**

- **Google 서버 (Authorization Server + Resource Server)**

그리고 아래와 같은 과정으로 진행됩니다.

<br/>

---

<br/>

### 1단계: 사용자 인증 요청

- 사용자가 `구글 로그인 버튼`을 클릭하면, 프론트엔드는 백엔드의 `/oauth2/authorization/google` 엔드포인트를 호출합니다.

- 이후 리다이렉트를 통해 Google 로그인 페이지로 이동하며, 사용자는 Google ID/PW를 입력하여 인증을 수행합니다.

<br/>

---

<br/>

### 2단계: 인가 코드 발급 및 전달

- 인증이 완료되면 Google 서버는 `인가 코드 (Authentication Code)`를 포함한 리디렉션 URL로 다시 돌아옵니다.

- 백엔드는 해당 `인가코드` 값을 파라미터로 받아 인증 요청을 처리합니다.

<br/>

---

<br/>

### 3단계: 액세스 토큰 및 유저 정보 획득

- 백엔드는 받은 `인가코드`를 이용해 Google 서버에 액세스 토큰을 요청합니다.

- Google 서버는 토큰을 발급하고, 해당 토큰으로부터 사용자 프로필 정보(이메일, 이름 등)를 요청하여 받아옵니다.

<br/>

---

<br/>

### 4단계: 자체 JWT 발급 및 저장

- 서비스 서버는 Google 유저 정보를 기반으로 자체 `User` 엔티티를 생성하거나 조회한 후, 자체 발급한 **JWT Access Token**과 **Refresh Token**을 생성합니다.

- 이 토큰은 다음과 같이 브라우저에 저장됩니다:

  - `Access Token`: localStorage 또는 쿠키

  - `Refresh Token`: HttpOnly 쿠키로 분리 저장

<br/>

---

<br/>

### 5단계: 로그인 완료 및 인증 상태 유지

- 토큰 저장이 완료되면, 브라우저는 인증된 상태로 서비스 접근이 가능해집니다.

- 이후 클라이언트는 모든 요청 시 `Access Token`을 함께 전송하며, 서버는 이를 검증하여 인가를 수행합니다.

- `Access Token`이 만료된 경우, `Refresh Token`을 이용해 재발급을 요청합니다.

<br/>

---

<br/>

### 보안 고려 사항

- JWT는 암호화되지 않으므로 반드시 HTTPS 위에서만 통신하도록 설정하였습니다.

- Refresh Token은 HttpOnly + Secure 쿠키에 저장되어 JavaScript로 접근할 수 없습니다.

- 인증 객체는 `SecurityContextHolder`에 저장되어 요청마다 전역적으로 접근이 가능하며,
  스레드 간 공유되지 않아 **성능 및 보안 측면 모두에서 유리**합니다.

<br/>
<br/>

## 결과

현재는 JWT 기반 OAuth2 인증 흐름을 안정적으로 운영하고 있습니다.

---

### 성능 측면 개선

- 매 요청마다 DB에서 사용자 정보를 조회하던 과정을 줄였습니다.

```
  기존: SecurityContext → Principal → getEmail → findByEmail → User 객체 획득
  개선: @AuthenticationPrincipal User me → 즉시 인증 객체 접근 가능
```

---

### 보안 취약점 발견 및 개선 진행 중

- 과거에는 `Access Token`을 URL 쿼리 파라미터로 전달하는 방식이 존재하였고,
  로그아웃 후 이전 페이지로 돌아가도 토큰이 재사용되는 문제가 있었습니다.

- 임시 해결책으로, **로컬스토리지에 토큰을 저장한 뒤 URL을 즉시 제거**하는 방식으로 대응하였으나,
  이는 보안상 근본적인 해결책이 아니며,
  향후 Access Token 또한 HttpOnly 쿠키로 전환하는 개선 작업을 계획 중입니다.

---

### 향후 도입 고려 사항 :`OIDC(OpenID Connect)`

- 현재는 OAuth2의 Authorization Code Flow만 사용하고 있으나,
  단순 로그인 기능만 필요한 상황에서는 OIDC가 더 적합할 수 있습니다.

- OAuth2 인증 플로우에 대한 충분한 이해를 위해 현재는 도입을 보류하고 있습니다.
