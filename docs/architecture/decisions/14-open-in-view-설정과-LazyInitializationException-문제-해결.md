# `open in view` 설정과 LazyInitializationException 문제 해결

Date: 2025-07-30

## 상태

적용 중

## 맥락

프로젝트 **DontGoBack**에서는 장기적으로 **마이크로서비스 아키텍처(MSA)**를 도입하기 위한 준비 단계로, 도메인 구조를 세분화하고자 했습니다. 그 일환으로 유저의 자산 정보를 담당하는 `UserAssetHistory` 엔티티를 새롭게 추가하고, 기존 `User`와의 연관관계를 정비하였습니다.

이 과정에서, 회원 가입 시 `AssetHistory`와 `AccountStatusHistory`를 함께 생성하여 연결하는 방식으로 구조를 개선하였으며, 로그인 이후 토큰을 생성하기 위해 `User` 엔티티를 `RefreshToken`에서 지연 로딩(LAZY) 방식으로 참조하는 로직이 포함되었습니다.

그러나 해당 변경사항을 배포 환경에 적용한 후, 최초 로그인 시 아래와 같은 예외가 발생하였습니다.

### 문제 상황

아래와 같이 LazyInitializationException 예외가 발생하였습니다.

```text
org.hibernate.LazyInitializationException: Could not initialize proxy [com.dontgoback.dontgo.domain.user.User#1] - no session
    at org.hibernate.proxy.AbstractLazyInitializer.initialize(AbstractLazyInitializer.java:174)
    at org.hibernate.proxy.AbstractLazyInitializer.getImplementation(AbstractLazyInitializer.java:328)
    at org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor.intercept(ByteBuddyInterceptor.java:44)
    at org.hibernate.proxy.ProxyConfiguration$InterceptorDispatcher.intercept(ProxyConfiguration.java:102)
    at com.dontgoback.dontgo.domain.user.User$HibernateProxy$MjS7gWH5.getEmail(Unknown Source)
    at com.dontgoback.dontgo.config.jwt.TokenProvider.makeToken(TokenProvider.java:48)
    at com.dontgoback.dontgo.config.jwt.TokenProvider.generateToken(TokenProvider.java:31)
```

특이한 점은 **최초 로그인에서만 이 예외가 발생**하고, 이후 재로그인 시에는 예외 없이 정상 작동하였다는 점입니다. 이로 인해 원인 파악에 큰 어려움을 겪었습니다.

### 원인 분석

과거 **대용량 테스트 데이터를 삽입하기 위한 배치 작업**을 진행하던 중,
다음과 같은 설정을 application.yml에 추가한 상태로 유지하고 있었습니다:

```yaml
spring:
  jpa:
    open-in-view: false # (test) flush 시점 명확히 하기 위해 설정한 값
```

- 해당 설정은 **트랜잭션이 종료된 이후에는 지연 로딩(LAZY)이 불가능하도록**  
  Hibernate의 영속성 컨텍스트(Session)을 강제로 종료시키는 역할을 합니다.

- 따라서 로그인 과정에서 `RefreshToken` 객체를 통해 User 엔티티를 조회한 이후,  
  그 세션이 닫힌 상태에서 `user.getEmail()` 과 같이 LAZY 필드에 접근하면서 예외가 발생한 것입니다.

## 결정

1. pring.jpa.open-in-view: true로 설정을 복구하여, 예외 발생을 일단 방지하였습니다.

2. 추후 open-in-view를 false로 유지하더라도 예외가 발생하지 않도록, Controller나 Filter 레벨에서 지연 로딩된 엔티티를 직접 사용하는 구조를 개선할 예정입니다.

3. TokenProvider가 User 도메인을 직접 사용하지 않도록 하고, Service 계층에서 DTO 기반으로 토큰 생성을 처리하도록 역할 분리를 수행하였습니다.

## 결과

- **서비스 정상화:** 설정을 되돌린 이후, 최초 로그인에서도 예외 없이 정상적으로 액세스 토큰과 리프레시 토큰이 발급되었습니다.

- **설계 개선 방향 도출:**

  - Controller/Filter에서는 반드시 DTO를 활용하고, Entity 직접 접근을 지양해야 함을 실감하게 되었습니다.

  - 도메인 모델 내에서 토큰 생성과 같은 작업을 수행하기보다는,  
    트랜잭션이 보장되는 Service 계층에서 DTO 기반으로 처리하는 방식이 안정적이라는 결론을 도출하였습니다.

- **향후 계획:**

  - 전체 인증 및 응답 처리 구조를 DTO 기반으로 점진적으로 전환할 예정입니다.

  - 서비스 내에서 지연 로딩이 필요한 경우 JOIN FETCH 또는 DTO Projection을 사용하여 명시적으로 데이터를 조회하도록 개선할 것입니다.